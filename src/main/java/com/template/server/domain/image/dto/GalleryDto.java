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
    private final Long commentCount;
    private final Long likeCount;
    private final int downloadCount;


    public static GalleryDto from(Gallery entity) {
        return new GalleryDto(
                entity.getGalleryId(),
                entity.getPrompt(),
                entity.getStorageUrl(),
                entity.getOption(),
                entity.getCreatedAt(),
                null,
                null,
                entity.getDownloadCount()
        );
    }

    public static GalleryDto from(Gallery entity, Long commentCount, Long likeCount) {
        return new GalleryDto(
                entity.getGalleryId(),
                entity.getPrompt(),
                entity.getStorageUrl(),
                entity.getOption(),
                entity.getCreatedAt(),
                commentCount,
                likeCount,
                entity.getDownloadCount()
        );
    }
}
