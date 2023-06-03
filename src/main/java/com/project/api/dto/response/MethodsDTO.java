package com.project.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MethodsDTO {

    private String name;
    private Integer id;
    private Boolean isDefault = Boolean.FALSE;
}
