package model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class Ride {

    private String source;
    private String destination;
    private String vehicleNumberPlate;
    private Long driverId;
    private Integer totalOfferedSeats;
    private Long rideOfferTimestamp;
    private List<Long> passengerIds;
}
