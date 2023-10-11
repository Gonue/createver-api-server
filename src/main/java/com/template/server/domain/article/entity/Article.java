package com.template.server.domain.article.entity;


import com.template.server.domain.member.entity.Member;
import com.template.server.global.audit.AuditingFields;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "article")
@Entity
public class Article extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id", updatable = false)
    private Long articleId;

    @Setter
    @Column(name = "title", nullable = false, columnDefinition = "TEXT", length = 200)
    private String title;

    @Setter
    @Column(name = "content", nullable = false, columnDefinition = "TEXT", length = 5000)
    private String content;

    @ManyToOne(optional = true) @Setter
    @JoinColumn(name = "member_id")
    private Member member;

    public static Article of(String title, String content, Member member){
        Article article = new Article();
        article.setTitle(title);
        article.setContent(content);
        article.setMember(member);
        return article;
    }
}
