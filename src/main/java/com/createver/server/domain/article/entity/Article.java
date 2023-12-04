package com.createver.server.domain.article.entity;


import com.createver.server.domain.member.entity.Member;
import com.createver.server.global.audit.AuditingFields;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "article",
        indexes = {@Index(name = "idx_article_created_at", columnList = "created_at")})
@Entity
public class Article extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id", updatable = false)
    private Long articleId;

    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "thumbnail_url", columnDefinition = "TEXT")
    private String thumbnailUrl;

    @ManyToOne(optional = true)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Article(String title, String content, Member member, String thumbnailUrl) {
        this.title = title;
        this.content = content;
        this.member = member;
        this.thumbnailUrl = thumbnailUrl;
    }

    public void updateArticle(String title, String content, String thumbnailUrl) {
        this.title = title;
        this.content = content;
        this.thumbnailUrl = thumbnailUrl;
    }
}
