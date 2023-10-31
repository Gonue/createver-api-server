package com.template.server.domain.image.dto.response;

import com.template.server.domain.image.dto.GalleryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GalleryResponse {

    private final Long galleryId;
    private final String prompt;
    private final String storageUrl;
    private final int option;
    private final LocalDateTime createdAt;
    private final Long commentCount;
    private final int likeCount;
    private final int downloadCount;

    public static GalleryResponse from(GalleryDto dto){
        return new GalleryResponse(
                dto.getGalleryId(),
                dto.getPrompt(),
                dto.getStorageUrl(),
                dto.getOption(),
                dto.getCreatedAt(),
                dto.getCommentCount(),
                dto.getLikeCount(),
                dto.getDownloadCount()
        );
    }
}
