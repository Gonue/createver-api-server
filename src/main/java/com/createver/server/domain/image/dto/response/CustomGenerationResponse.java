package com.createver.server.domain.image.dto.response;

import com.createver.server.global.util.aws.CloudFrontUrlUtils;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomGenerationResponse {

    private Long galleryId;
    private String prompt;
    private String s3Url;
    private int option;
    private LocalDateTime createdAt;

    public String getS3Url() {
        return CloudFrontUrlUtils.convertToCloudFrontUrl(this.s3Url);
    }
}
