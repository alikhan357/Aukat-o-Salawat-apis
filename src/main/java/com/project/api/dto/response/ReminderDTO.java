package com.project.api.dto.response;

import lombok.*;
import org.springframework.lang.NonNull;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ReminderDTO {

    private String audioFile;
    private String type;
    private String time;
    private String namaz;
    private Boolean isEnabled;
    private Integer adjustedTime;
    private String audioUrl;
    private String timeZone;
    private String createdDate;
    private String updatedDate;
}
