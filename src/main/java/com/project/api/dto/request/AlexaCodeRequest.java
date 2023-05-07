package com.project.api.dto.request;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AlexaCodeRequest {
    private String code;
    private String deviceId;
}
