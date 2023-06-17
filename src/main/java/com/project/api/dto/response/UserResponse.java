package com.project.api.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserResponse {

    private String firstName;
    private String lastname;
    private String serialNumber;
    private String email;
    private MethodsDTO method;
    private MethodsDTO school;
    private Double lat;
    private Double lng;
    private String timeZone;
}
