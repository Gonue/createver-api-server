package com.createver.server.domain.image.dto;

import com.createver.server.domain.image.entity.Gallery;
import com.createver.server.global.util.aws.CloudFrontUrlUtils;
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
    private final int likeCount;
    private final int downloadCount;
    private final int reportCount;
    private final boolean isBlinded;

    public static GalleryDto from(Gallery entity) {
        String cloudFrontUrl = CloudFrontUrlUtils.convertToCloudFrontUrl(entity.getStorageUrl());
        return new GalleryDto(
                entity.getGalleryId(),
                entity.getPrompt(),
                cloudFrontUrl,
                entity.getOption(),
                entity.getCreatedAt(),
                null,
                entity.getLikeCount(),
                entity.getDownloadCount(),
                entity.getReportCount(),
                entity.isBlinded()
        );
    }
}
