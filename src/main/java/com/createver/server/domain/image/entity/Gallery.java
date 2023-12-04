package com.createver.server.domain.image.entity;

import com.createver.server.domain.member.entity.Member;
import com.createver.server.global.audit.AuditingFields;
import lombok.*;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "gallery",
        indexes = {@Index(name = "idx_gallery_created_at", columnList = "created_at")})
@Entity
public class Gallery extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gallery_id", updatable = false)
    private Long galleryId;

    @Column(name = "prompt", nullable = false, columnDefinition = "TEXT")
    private String prompt;

    @Column(name = "storage_url", nullable = false, columnDefinition = "TEXT")
    private String storageUrl;

    @Column(name = "image_option", nullable = false)
    private int option;

    @ManyToMany
    @JoinTable(
            name = "gallery_tag",
            joinColumns = @JoinColumn(name = "gallery_id"),
            inverseJoinColumns = @JoinColumn(name = "image_tag_id")
    )
    private List<ImageTag> tags = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "download_count", columnDefinition = "int default 0")
    private int downloadCount = 0;

    @Column(name = "like_count", columnDefinition = "int default 0")
    private int likeCount = 0;

    @Column(name = "report_count", columnDefinition = "int default 0")
    private int reportCount = 0;

    @Column(name = "is_blinded", columnDefinition = "boolean default false")
    private boolean isBlinded = false;

    @Builder
    public Gallery(String prompt, String storageUrl, int option, List<ImageTag> tags, Member member) {
        this.prompt = prompt;
        this.storageUrl = storageUrl;
        this.option = option;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.member = member;
    }

    public void updateBlindStatus(boolean isBlinded) {
        this.isBlinded = isBlinded;
    }

    public void increaseDownloadCount() {
        this.downloadCount += 1;
    }

    public void increaseLikeCount() {
        this.likeCount += 1;
    }

    public void increaseReportCount() {
        this.reportCount += 1;
        if (this.reportCount > 5) {
            this.isBlinded = true;
        }
    }

}
