package com.project.api.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PasswordRequest {
    private String email;
    private String password;
    private String otp;
}
