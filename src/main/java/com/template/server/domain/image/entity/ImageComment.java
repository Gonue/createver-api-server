package com.template.server.domain.image.entity;

import com.template.server.domain.member.entity.Member;
import com.template.server.global.audit.AuditingFields;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


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

    @Setter @Column(name = "content", nullable = false)
    private String content;

    @Setter @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gallery_id")
    private Gallery gallery;

    @Setter @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public static ImageComment create(String content, Gallery gallery, Member member) {
        ImageComment comment = new ImageComment();
        comment.setContent(content);
        comment.setGallery(gallery);
        comment.setMember(member);
        return comment;
    }
}
