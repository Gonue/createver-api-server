package com.template.server.domain.music.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlbumCreateRequest {
    private String title;
    private String imageUrl;
    private String musicUrl;
}
