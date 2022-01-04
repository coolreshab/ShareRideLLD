package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Address {

    private String postalCode;
    private String streetDetails;
    private String city;
    private String state;
    private String email;
    private Long mobileNumber;
}
