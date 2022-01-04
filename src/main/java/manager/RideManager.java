package manager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import constant.SelectionStrategy;
import dao.RideDAO;
import dao.UserDAO;
import dao.VehicleDAO;
import exception.EndRideException;
import exception.OfferRideException;
import exception.RideSelectionException;
import lombok.extern.slf4j.Slf4j;
import model.Ride;
import model.RideSelection;
import model.RideStat;
import model.Vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Singleton
public class RideManager {

    private final RideDAO rideDAO;
    private final VehicleDAO vehicleDAO;
    private final UserDAO userDAO;

    @Inject
    public RideManager(final RideDAO rideDAO,
                       final VehicleDAO vehicleDAO,
                       final UserDAO userDAO) {
        this.rideDAO = rideDAO;
        this.vehicleDAO = vehicleDAO;
        this.userDAO = userDAO;
    }

    public void offerRide(Ride ride) throws OfferRideException {
        Vehicle vehicle = vehicleDAO.get(ride.getVehicleNumberPlate());
        if (vehicle == null) {
            String errorMessage = String.format("Vehicle %s not registered with the system",
                    ride.getVehicleNumberPlate());
            log.error(errorMessage);
            throw new OfferRideException(errorMessage);
        }
        ride.setDriverId(vehicle.getOwnerId());
        if (vehicle.getCapacity() < ride.getTotalOfferedSeats() + 1) {
            String errorMessage = String.format("Vehicle %s cannot offer %s seats",
                    ride.getVehicleNumberPlate(), ride.getTotalOfferedSeats());
            log.error(errorMessage);
            throw new OfferRideException(errorMessage);
        }
        ride.setRideOfferTimestamp(System.currentTimeMillis());
        ride.setPassengerIds(new ArrayList<>());
        rideDAO.offerRide(ride);
    }

    public void endRide(String vehicleNumber) throws EndRideException {
        rideDAO.endRide(vehicleNumber);
    }

    public Map<String, RideStat> getRideStats() {
        Map<Long, RideStat> ownerIdToRideStat = rideDAO.getRideStats();
        Map<String, RideStat> ownerNameToRideStat = new HashMap<>();
        for (Long ownerId : ownerIdToRideStat.keySet()) {
            ownerNameToRideStat.put(userDAO.get(ownerId).getName(), ownerIdToRideStat.get(ownerId));
        }
        return ownerNameToRideStat;
    }

    public Ride selectRide(RideSelection rideSelection) throws RideSelectionException {
        if (rideSelection.getPassengerIds().size() > 2) {
            String errorMessage = "System cannot select more than 2 seats";
            log.error(errorMessage);
            throw new RideSelectionException(errorMessage);
        }
        List<Ride> nominees = rideDAO.getZeroStopPaths(rideSelection.getSource(),
                rideSelection.getDestination(), rideSelection.getPassengerIds().size());
        Ride selectedRide = null;
        if (rideSelection.getStrategy().equals(SelectionStrategy.MODEL)) {
            selectedRide = nominees.stream().filter(x -> vehicleDAO.get(x.getVehicleNumberPlate())
                    .getModel().equals(rideSelection.getVehicleModel()))
                    .findAny().orElse(null);
        } else {
            for (Ride ride : nominees) {
                if (selectedRide == null || ride.getTotalOfferedSeats() - ride.getPassengerIds().size()
                        > selectedRide.getTotalOfferedSeats() - selectedRide.getPassengerIds().size()) {
                    selectedRide = ride;
                }
            }
        }
        if (selectedRide != null) {
            rideDAO.bookRide(selectedRide.getVehicleNumberPlate(), rideSelection.getPassengerIds());
        }
        return selectedRide;
    }
}
