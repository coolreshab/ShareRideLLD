import com.google.inject.Guice;
import com.google.inject.Injector;
import constant.Gender;
import constant.SelectionStrategy;
import dao.UserDAO;
import dao.VehicleDAO;
import guice.module.DataLayerModule;
import lombok.extern.slf4j.Slf4j;
import manager.RideManager;
import model.Ride;
import model.RideSelection;
import model.User;
import model.Vehicle;

import java.util.Collections;
import java.util.Scanner;

@Slf4j
public class ShareRideApplication {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new DataLayerModule());
        UserDAO userDAO = injector.getInstance(UserDAO.class);
        VehicleDAO vehicleDAO = injector.getInstance(VehicleDAO.class);
        RideManager rideManager = injector.getInstance(RideManager.class);

        Scanner scanner = new Scanner(System.in);
        int testCases = scanner.nextInt();
        for (int i = 0; i < testCases; ++i) {
            int queryType = scanner.nextInt();
            if (queryType == 1) {
                registerUser(userDAO, scanner);
            } else if (queryType == 2) {
                registerVehicle(vehicleDAO, scanner);
            } else if (queryType == 3) {
                offerRide(rideManager, scanner);
            } else if (queryType == 4) {
                selectRide(rideManager, scanner);
            } else if (queryType == 5) {
                endRide(rideManager, scanner);
            } else {
                printStats(rideManager);
            }
        }
    }

    private static void registerUser(UserDAO userDAO, Scanner scanner) {
        try {
            Long aadharNo = scanner.nextLong();
            String name = scanner.next();
            char gender = scanner.next().charAt(0);
            Integer age = scanner.nextInt();
            userDAO.insert(new User(aadharNo, name, gender == 'M' ? Gender.MALE : Gender.FEMALE,
                    age, null));
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    private static void registerVehicle(VehicleDAO vehicleDAO, Scanner scanner) {
        try {
            String numberPlate = scanner.next();
            String model = scanner.next();
            Long aadharNo = scanner.nextLong();
            Integer capacity = scanner.nextInt();
            vehicleDAO.insert(new Vehicle(numberPlate, model, aadharNo, capacity));
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    private static void offerRide(RideManager rideManager, Scanner scanner) {
        try {
            String source = scanner.next();
            String destination = scanner.next();
            String vehicleNo = scanner.next();
            Integer seats = scanner.nextInt();
            rideManager.offerRide(Ride.builder().source(source)
                    .destination(destination).vehicleNumberPlate(vehicleNo)
                    .totalOfferedSeats(seats).build());
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    private static void selectRide(RideManager rideManager, Scanner scanner) {
        try {
            String source = scanner.next();
            String destination = scanner.next();
            Long aadharId = scanner.nextLong();
            String strategy = scanner.next();
            String vehicleModel = null;
            SelectionStrategy selectionStrategy = SelectionStrategy.MOST_VACANT;
            if (strategy.equals(SelectionStrategy.MODEL.name())) {
                vehicleModel = scanner.next();
                selectionStrategy = SelectionStrategy.MODEL;
            }
            Ride selectedRide = rideManager.selectRide(new RideSelection(source, destination,
                    Collections.singletonList(aadharId), selectionStrategy, vehicleModel));
            if (selectedRide == null) {
                log.info("No ride found");
            } else {
                log.info(selectedRide.toString());
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    private static void endRide(RideManager rideManager, Scanner scanner) {
        try {
            String vehicleNo = scanner.next();
            rideManager.endRide(vehicleNo);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    private static void printStats(RideManager rideManager) {
        try {
            log.info(rideManager.getRideStats().toString());
        } catch (Exception e) {
            log.error(e.toString());
        }
    }
}
