package com.template.server.domain.image.dto.response;

import com.template.server.domain.image.dto.GalleryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GalleryRecommendationResponse {

    private final Long galleryId;
    private final String prompt;
    private final String storageUrl;
    private final int option;
    private final LocalDateTime createdAt;
    private final Long commentCount;
    private final int likeCount;
    private final int downloadCount;
    private final double score;
    private final int reportCount;
    private final boolean isBlinded;

    public static GalleryRecommendationResponse from(GalleryDto dto){
        long commentCount = dto.getCommentCount() == null ? 0 : dto.getCommentCount();
        double score = commentCount * 0.1 + dto.getLikeCount() * 0.2 + dto.getDownloadCount() * 0.7;
        return new GalleryRecommendationResponse(
                dto.getGalleryId(),
                dto.getPrompt(),
                dto.getStorageUrl(),
                dto.getOption(),
                dto.getCreatedAt(),
                commentCount,
                dto.getLikeCount(),
                dto.getDownloadCount(),
                score,
                dto.getReportCount(),
                dto.isBlinded()
        );
    }
}
