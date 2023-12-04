package com.template.server.domain.article.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArticleUpdateRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotBlank
    private String thumbnailUrl;
}
