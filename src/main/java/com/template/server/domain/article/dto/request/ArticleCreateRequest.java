package com.template.server.domain.article.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArticleCreateRequest {
    private String title;
    private String content;
    private String thumbnailUrl;
}
