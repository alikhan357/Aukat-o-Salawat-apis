package com.project.api.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private String firstName;
    private String lastname;
    private String macAddress;
    private String email;
    private MethodsDTO method;
    private MethodsDTO school;
    private Double lat;
    private Double lng;
}
