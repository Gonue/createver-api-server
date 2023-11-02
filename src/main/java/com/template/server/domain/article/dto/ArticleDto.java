package com.template.server.domain.article.dto;

import com.template.server.domain.article.entity.Article;
import com.template.server.domain.member.dto.MemberDto;
import com.template.server.global.util.CloudFrontUrlUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.template.server.global.util.CloudFrontUrlUtils.convertImgUrlsInHtmlContent;

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
        String convertedContent = convertImgUrlsInHtmlContent(entity.getContent());
        return new ArticleDto(
                entity.getArticleId(),
                entity.getTitle(),
                convertedContent,
                MemberDto.from(entity.getMember()),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }
}
