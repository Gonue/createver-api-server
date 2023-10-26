package com.template.server.domain.image.entity;

import com.template.server.domain.member.entity.Member;
import com.template.server.global.audit.AuditingFields;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "image_like",
        indexes = {@Index(name = "idx_image_like_created_at", columnList = "created_at")})
@Entity
public class ImageLike extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id", updatable = false)
    private Long likeId;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gallery_id")
    private Gallery gallery;

    public static ImageLike create(Member member, Gallery gallery) {
        ImageLike imageLike = new ImageLike();
        imageLike.setMember(member);
        imageLike.setGallery(gallery);
        return imageLike;
    }
}
