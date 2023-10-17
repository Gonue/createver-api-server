package com.template.server.domain.image.dto;

import com.template.server.domain.image.entity.Gallery;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GalleryDto {

    private final Long galleryId;
    private final String prompt;
    private final String storageUrl;
    private final int option;
    private final LocalDateTime createdAt;

    public static GalleryDto from(Gallery entity){
        return new GalleryDto(
                entity.getGalleryId(),
                entity.getPrompt(),
                entity.getStorageUrl(),
                entity.getOption(),
                entity.getCreatedAt()
        );
    }
}
