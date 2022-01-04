package model;

import constant.SelectionStrategy;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RideSelection {

    private final String source;
    private final String destination;
    private final List<Long> passengerIds;
    private final SelectionStrategy strategy;
    private final String vehicleModel;
}
