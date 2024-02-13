package com.createver.server.domain.banner.entity;

import com.createver.server.global.audit.AuditingFields;
import com.createver.server.global.util.convert.StringListConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "banner")
@Entity
public class Banner extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "banner_id", updatable = false)
    private Long bannerId;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Convert(converter = StringListConverter.class)
    private List<String> tags;

    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "position", nullable = false)
    private String position;

    @Column(name = "order_sequence", nullable = false)
    private int orderSequence;

    @Builder
    public Banner(String imageUrl, List<String> tags, String title, String content, boolean active, String position, int orderSequence) {
        this.imageUrl = imageUrl;
        this.tags = tags;
        this.title = title;
        this.content = content;
        this.active = active;
        this.position = position;
        this.orderSequence = orderSequence;
    }

    public void updateBanner(String imageUrl, List<String> tags, String title, String content, boolean active, String position, int orderSequence) {
        this.imageUrl = imageUrl;
        this.tags = tags;
        this.title = title;
        this.content = content;
        this.active = active;
        this.position = position;
        this.orderSequence = orderSequence;
    }

    public void updateOrder(int orderSequence) {
        this.orderSequence = orderSequence;
    }
}
