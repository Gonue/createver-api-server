package com.createver.server.domain.article.dto.response;

import com.createver.server.domain.article.dto.ArticleDto;
import com.createver.server.domain.member.dto.response.MemberResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ArticleResponse {

    private final Long articleId;
    private final String title;
    private final String content;
    private final String thumbnailUrl;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private final MemberResponse member;

    public static ArticleResponse from(ArticleDto dto){
        return new ArticleResponse(
                dto.getArticleId(),
                dto.getTitle(),
                dto.getContent(),
                dto.getThumbnailUrl(),
                dto.getCreatedAt(),
                dto.getModifiedAt(),
                MemberResponse.from(dto.getMember())
        );
    }
}
