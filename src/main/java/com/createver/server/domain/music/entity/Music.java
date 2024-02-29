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
@Table(name = "music")
@Entity
public class Music  extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "music_id", updatable = false)
    private Long musicId;

    @Column(name = "prompt", nullable = false)
    private String prompt;

    @Column(name = "result_music_url")
    private String resultMusicUrl;

    @Column(name = "status")
    private String status;

    @Column(name = "prediction_id")
    private String predictionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Music(String prompt, String resultMusicUrl, String status, String predictionId, Member member) {
        this.prompt = prompt;
        this.resultMusicUrl = resultMusicUrl;
        this.status = status;
        this.predictionId = predictionId;
        this.member = member;
    }

    public void updateResultMusicAndStatus(String resultMusicUrl, String status){
        this.resultMusicUrl = resultMusicUrl;
        this.status = status;
    }
}
