package com.template.server.domain.image.entity;

import com.template.server.domain.member.entity.Member;
import com.template.server.global.audit.AuditingFields;
import lombok.*;

import jakarta.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "image_comment",
        indexes = {@Index(name = "idx_image_comment_created_at", columnList = "created_at")})
@Entity
public class ImageComment extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", updatable = false)
    private Long commentId;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gallery_id")
    private Gallery gallery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public ImageComment(String content, Gallery gallery, Member member) {
        this.content = content;
        this.gallery = gallery;
        this.member = member;
    }
    public void updateContent(String content) {
        this.content = content;
    }
}
