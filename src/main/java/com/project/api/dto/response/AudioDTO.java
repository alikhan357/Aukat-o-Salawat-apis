package com.project.api.dto.response;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AudioDTO {

    private String id;
    private String name;
    private Long size;
    private String createdDate;
    private String updatedDate;
    private Boolean isFav = Boolean.FALSE;
    private Boolean isPlaying = Boolean.FALSE;
}
