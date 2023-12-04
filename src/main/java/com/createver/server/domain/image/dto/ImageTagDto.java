package com.createver.server.domain.image.dto;

import com.createver.server.domain.image.entity.ImageTag;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageTagDto {
    private final Long tagId;
    private final String name;

    public static ImageTagDto from(ImageTag entity){
        return new ImageTagDto(
                entity.getTagId(),
                entity.getName()
        );
    }
}
