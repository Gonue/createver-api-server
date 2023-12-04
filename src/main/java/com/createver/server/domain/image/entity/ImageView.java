package com.createver.server.domain.image.entity;

import com.createver.server.domain.member.entity.Member;
import com.createver.server.global.audit.AuditingFields;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "image_view",
       indexes = {@Index(name = "idx_member", columnList = "member_id"),
                  @Index(name = "idx_gallery", columnList = "gallery_id"),
                  @Index(name = "idx_viewed_at", columnList = "viewed_at")})
@Entity
public class ImageView extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "view_id", updatable = false)
    private Long viewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gallery_id")
    private Gallery gallery;

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;

    @Builder
    public ImageView(Member member, Gallery gallery, LocalDateTime viewedAt) {
        this.member = member;
        this.gallery = gallery;
        this.viewedAt = viewedAt;
    }
}
