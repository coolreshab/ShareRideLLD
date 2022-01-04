package model;

import constant.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User {

    private Long aadharNumber;
    private String name;
    private Gender gender;
    private Integer age;
    private Address address;
}