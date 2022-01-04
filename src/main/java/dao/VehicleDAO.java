package dao;

import model.Vehicle;

import java.util.Map;

public class VehicleDAO {

    private final Map<String, Vehicle> vehicles;

    public VehicleDAO(final Map<String, Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public void insert(Vehicle vehicle) {
        vehicles.put(vehicle.getNumberPlate(), vehicle);
    }

    public Vehicle get(String numberPlate) {
        return vehicles.get(numberPlate);
    }
}
