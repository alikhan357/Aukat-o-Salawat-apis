package com.project.api.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DynamoDBDocument
@NoArgsConstructor
@Getter
@Setter
public class Audio {

    @DynamoDBAttribute
    private String name;

    @DynamoDBAttribute
    private Long size;

    @DynamoDBAttribute(attributeName = "created_date")
    private String createdDate;

    @DynamoDBAttribute(attributeName = "updated_date")
    private String updatedDate;


}
