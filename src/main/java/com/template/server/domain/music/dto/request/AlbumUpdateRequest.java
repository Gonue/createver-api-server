package com.template.server.domain.music.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlbumUpdateRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String imageUrl;
    @NotBlank
    private String musicUrl;
}
