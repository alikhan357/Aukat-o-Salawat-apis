package com.project.api.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MethodsDTO {

    private String name;
    private Integer id;
    private Boolean isDefault = Boolean.FALSE;
}
