package com.template.server.domain.image.entity;

import com.template.server.domain.member.entity.Member;
import com.template.server.global.audit.AuditingFields;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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

    @Setter @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Setter @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gallery_id")
    private Gallery gallery;

    @Setter @Column(name = "viewed_at")
    private LocalDateTime viewedAt;

    public static ImageView create(Member member, Gallery gallery) {
        ImageView imageView = new ImageView();
        imageView.setMember(member);
        imageView.setGallery(gallery);
        imageView.setViewedAt(LocalDateTime.now());
        return imageView;
    }
}
