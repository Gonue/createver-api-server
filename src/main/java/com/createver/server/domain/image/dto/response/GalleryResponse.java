package com.createver.server.domain.image.dto.response;

import com.createver.server.domain.image.dto.GalleryDto;
import com.createver.server.global.util.aws.CloudFrontUrlUtils;
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
    private final int reportCount;
    private final boolean isBlinded;

    public static GalleryResponse from(GalleryDto dto){
        String cloudFrontUrl = CloudFrontUrlUtils.convertToCloudFrontUrl(dto.getStorageUrl());
        return new GalleryResponse(
                dto.getGalleryId(),
                dto.getPrompt(),
                cloudFrontUrl,
                dto.getOption(),
                dto.getCreatedAt(),
                dto.getCommentCount(),
                dto.getLikeCount(),
                dto.getDownloadCount(),
                dto.getReportCount(),
                dto.isBlinded()
        );
    }
}
