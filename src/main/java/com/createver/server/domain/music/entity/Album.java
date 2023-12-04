package com.createver.server.domain.music.entity;

import com.createver.server.domain.member.entity.Member;
import com.createver.server.global.audit.AuditingFields;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "album")
@Entity
public class Album extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "album_id", updatable = false)
    private Long albumId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "music_url", nullable = false, columnDefinition = "TEXT")
    private String musicUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Album(String title, String imageUrl, String musicUrl, Member member) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.musicUrl = musicUrl;
        this.member = member;
    }

    public void updateAlbum(String title, String imageUrl, String musicUrl){
        this.title = title;
        this.imageUrl = imageUrl;
        this.musicUrl = musicUrl;
    }
}
