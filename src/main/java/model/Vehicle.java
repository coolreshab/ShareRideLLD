package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Vehicle {

    private String numberPlate;
    private String model;
    private Long ownerId;
    private Integer capacity;
}
