package com.template.server.domain.image.entity;

import com.template.server.domain.member.entity.Member;
import com.template.server.global.audit.AuditingFields;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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

    @Setter
    @Column(name = "prompt", nullable = false, columnDefinition = "TEXT")
    private String prompt;

    @Setter
    @Column(name = "storage_url", nullable = false, columnDefinition = "TEXT")
    private String storageUrl;

    @Setter
    @Column(name = "image_option", nullable = false)
    private int option;


    @ManyToMany
    @Setter
    @JoinTable(
            name = "gallery_tag",
            joinColumns = @JoinColumn(name = "gallery_id"),
            inverseJoinColumns = @JoinColumn(name = "image_tag_id")
    )
    private List<ImageTag> tags = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "download_count", columnDefinition = "int default 0")
    private int downloadCount = 0;

    @Column(name = "like_count", columnDefinition = "int default 0")
    private int likeCount = 0;

    public void increaseDownloadCount() {
        this.downloadCount += 1;
    }

    public void increaseLikeCount() {
        this.likeCount += 1;
    }

    public static Gallery create(String prompt, String url, int option) {
        Gallery gallery = new Gallery();
        gallery.setPrompt(prompt);
        gallery.setStorageUrl(url);
        gallery.setOption(option);
        return gallery;
    }
}
