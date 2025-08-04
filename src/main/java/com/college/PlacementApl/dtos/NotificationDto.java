package com.college.PlacementApl.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {

    private String firstName;
    private String lastName;
    private String companyName;
    private String status;
    private String feedback;
    private String jobPosition;
    private String email;
    private String phoneNumber;


}
