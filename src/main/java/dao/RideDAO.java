package dao;

import exception.EndRideException;
import exception.OfferRideException;
import lombok.extern.slf4j.Slf4j;
import model.Ride;
import model.RideStat;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class RideDAO {

    private final Map<String, Ride> activeRides;
    private final Map<String, Set<String>> activeRideNetwork;
    private final List<Ride> passiveRides;

    public RideDAO(final Map<String, Ride> activeRides,
                   final Map<String, Set<String>> activeRideNetwork,
                   final List<Ride> passiveRides) {
        this.activeRides = activeRides;
        this.activeRideNetwork = activeRideNetwork;
        this.passiveRides = passiveRides;
    }

    public void offerRide(Ride ride) throws OfferRideException {
        if (activeRides.containsKey(ride.getVehicleNumberPlate())) {
            log.error("Ride {} already active", ride);
            throw new OfferRideException(String.format("Vehicle %s already used in active ride",
                    ride.getVehicleNumberPlate()));
        } else {
            activeRides.put(ride.getVehicleNumberPlate(), ride);
            Set<String> activeSourceVehicles = activeRideNetwork.getOrDefault(ride.getSource(), new HashSet<>());
            activeSourceVehicles.add(ride.getVehicleNumberPlate());
            activeRideNetwork.put(ride.getSource(), activeSourceVehicles);
        }
    }

    public void endRide(String vehicleNumber) throws EndRideException {
        if (activeRides.containsKey(vehicleNumber)) {
            Ride rideToEnd = activeRides.get(vehicleNumber);
            activeRides.remove(vehicleNumber);
            passiveRides.add(rideToEnd);
            activeRideNetwork.get(rideToEnd.getSource()).remove(vehicleNumber);
        } else {
            String errorMessage = String.format("Vehicle %s currently not available in active rides",
                    vehicleNumber);
            log.error(errorMessage);
            throw new EndRideException(errorMessage);
        }
    }

    public List<Ride> getZeroStopPaths(String source, String destination, Integer seats) {
        return activeRideNetwork.getOrDefault(source, new HashSet<>())
                .stream()
                .map(activeRides::get)
                .filter(x -> x.getDestination().equals(destination)
                        && x.getPassengerIds().size()
                        + seats <= x.getTotalOfferedSeats())
                .collect(Collectors.toList());
    }

    public List<Ride> getMultiStopPath(String source, String destination, Integer seats) {
        // TODO : Implement BFS to get min multi stops ride
        return Collections.emptyList();
    }

    public Map<Long, RideStat> getRideStats() {
        Map<Long, RideStat> userToRideStat = new HashMap<>();
        for (Ride ride : passiveRides) {
            RideStat driverRideStat = userToRideStat.getOrDefault(ride.getDriverId(), new RideStat());
            driverRideStat.setRidesOffered(driverRideStat.getRidesOffered() + 1);
            userToRideStat.put(ride.getDriverId(), driverRideStat);

            for (Long passengerId : ride.getPassengerIds()) {
                RideStat passengerRideStat = userToRideStat.getOrDefault(passengerId, new RideStat());
                passengerRideStat.setRidesTaken(passengerRideStat.getRidesTaken() + 1);
                userToRideStat.put(passengerId, passengerRideStat);
            }
        }
        return userToRideStat;
    }

    public void bookRide(String vehicleNumber, List<Long> passengers) {
        activeRides.get(vehicleNumber).getPassengerIds().addAll(passengers);
    }
}
