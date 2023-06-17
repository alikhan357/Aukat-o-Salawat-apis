package com.project.api.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class OTPValidateRequest {

    String email;
    String otp;
}
