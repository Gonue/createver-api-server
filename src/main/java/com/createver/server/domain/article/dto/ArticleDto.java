package com.createver.server.domain.article.dto;

import com.createver.server.domain.article.entity.Article;
import com.createver.server.domain.member.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDto  {
    private Long articleId;
    private String title;
    private String content;
    private MemberDto member;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static ArticleDto from(Article entity){
        return new ArticleDto(
                entity.getArticleId(),
                entity.getTitle(),
                entity.getContent(),
                MemberDto.from(entity.getMember()),
                entity.getThumbnailUrl(),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }
}
