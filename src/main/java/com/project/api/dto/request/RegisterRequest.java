package com.project.api.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RegisterRequest {

    private String firstName;
    private String lastname;
    private String macAddress;
    private String email;
    private String password;
}
