package com.project.api.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.NonNull;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class NamazTimeRequest {

    @NonNull
    private Double lat;
    @NonNull
    private Double lng;
    private Long method;
    private Long school;
    @NonNull
    private String timeZone;
}
