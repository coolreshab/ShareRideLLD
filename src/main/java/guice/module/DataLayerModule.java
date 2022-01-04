package guice.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import dao.RideDAO;
import dao.UserDAO;
import dao.VehicleDAO;

import java.util.ArrayList;
import java.util.HashMap;

public class DataLayerModule extends AbstractModule {

    @Provides
    @Singleton
    public VehicleDAO getVehicleDAO() {
        return new VehicleDAO(new HashMap<>());
    }

    @Provides
    @Singleton
    public UserDAO getUserDAO() {
        return new UserDAO(new HashMap<>());
    }

    @Provides
    @Singleton
    public RideDAO getRideDAO() {
        return new RideDAO(new HashMap<>(), new HashMap<>(), new ArrayList<>());
    }
}
