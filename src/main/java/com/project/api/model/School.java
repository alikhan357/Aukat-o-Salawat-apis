package com.project.api.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DynamoDBDocument
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class School {

    @DynamoDBAttribute
    private Integer id;

    @DynamoDBAttribute
    private String name;

}
