package com.template.server.domain.image.entity;

import com.template.server.domain.member.entity.Member;
import com.template.server.global.audit.AuditingFields;
import lombok.*;

import jakarta.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "image_report")
public class ImageReport extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id", updatable = false)
    private Long reportId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gallery_id")
    private Gallery gallery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


    @Builder
    public ImageReport(String content, Gallery gallery, Member member) {
        this.content = content;
        this.gallery = gallery;
        this.member = member;
    }
}