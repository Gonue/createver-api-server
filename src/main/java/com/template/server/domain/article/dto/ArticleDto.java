package com.template.server.domain.article.dto;

import com.template.server.domain.article.entity.Article;
import com.template.server.domain.member.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ArticleDto {
    private final Long articleId;
    private final String title;
    private final String content;
    private final MemberDto member;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public static ArticleDto from(Article entity){
        return new ArticleDto(
                entity.getArticleId(),
                entity.getTitle(),
                entity.getContent(),
                MemberDto.from(entity.getMember()),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }
}
