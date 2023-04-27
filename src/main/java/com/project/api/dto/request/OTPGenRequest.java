package com.project.api.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class OTPGenRequest {

    String email;
}
