package com.template.server.domain.image.dto.response;

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
}
