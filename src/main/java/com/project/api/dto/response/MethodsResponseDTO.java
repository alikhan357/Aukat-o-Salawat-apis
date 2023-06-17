package com.project.api.dto.response;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MethodsResponseDTO {

    List<MethodsDTO> methods;
    List<MethodsDTO> schools;

}
