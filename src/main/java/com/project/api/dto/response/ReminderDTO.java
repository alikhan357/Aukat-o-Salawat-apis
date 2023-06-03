package com.project.api.dto.response;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;
import org.springframework.lang.NonNull;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor

public class ReminderDTO {

    private String audioFile;
    private String type;
    private String time;
    private String namaz;
    private Boolean isEnabled;
    private Integer adjustedTime;
}
