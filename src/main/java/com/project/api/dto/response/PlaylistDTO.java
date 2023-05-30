package com.project.api.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class PlaylistDTO {

    private List<AudioDTO> audios;
    private String createdDate;
    private String updatedDate;
}
