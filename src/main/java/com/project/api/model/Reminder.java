package com.project.api.model;

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
@DynamoDBTable(tableName = "reminder")
public class Reminder {

    @DynamoDBHashKey(attributeName = "id")
    @DynamoDBAutoGeneratedKey
    private String id;
    @DynamoDBAttribute
    private String email;
    @DynamoDBAttribute(attributeName = "audio_file")
    @NonNull
    private String audioFile;
    @DynamoDBAttribute
    @NonNull
    private String type;
    @DynamoDBAttribute
    @NonNull
    private String time;
    @NonNull
    private String namaz;

}