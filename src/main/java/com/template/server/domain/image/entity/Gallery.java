package com.template.server.domain.image.entity;

import com.template.server.global.audit.AuditingFields;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "gallery")
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

    public static Gallery create(String prompt, String url, int option) {
        Gallery gallery = new Gallery();
        gallery.setPrompt(prompt);
        gallery.setStorageUrl(url);
        gallery.setOption(option);
        return gallery;
    }
}
