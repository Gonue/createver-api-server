package com.createver.server.domain.image.entity;

import com.createver.server.domain.member.entity.Member;
import com.createver.server.global.audit.AuditingFields;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "image_avatar")
@Entity
public class ImageAvatar extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "avatar_id", updatable = false)
    private Long avatarId;

    @Column(name = "prompt", nullable = false)
    private String prompt;

    @Column(name = "num_steps", nullable = false)
    private Integer numSteps;

    @Column(name = "style_name", nullable = false)
    private String styleName;

    @Column(name = "input_image", nullable = false)
    private String inputImage;

    @Column(name = "num_outputs", nullable = false)
    private Integer numOutputs;

    @Column(name = "guidance_scale", nullable = false)
    private Integer guidanceScale;

    @Column(name = "negative_prompt", nullable = false)
    private String negativePrompt;

    @Column(name = "style_strength_ratio", nullable = false)
    private Integer styleStrengthRatio;

    @Column(name = "result_image_url")
    private String resultImageUrl;

    @Column(name = "status")
    private String status;

    @Column(name = "prediction_id")
    private String predictionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public ImageAvatar(String prompt, Integer numSteps, String styleName, String inputImage, Integer numOutputs, Integer guidanceScale, String negativePrompt, Integer styleStrengthRatio, String resultImageUrl, String status, String predictionId, Member member) {
        this.prompt = prompt;
        this.numSteps = numSteps;
        this.styleName = styleName;
        this.inputImage = inputImage;
        this.numOutputs = numOutputs;
        this.guidanceScale = guidanceScale;
        this.negativePrompt = negativePrompt;
        this.styleStrengthRatio = styleStrengthRatio;
        this.resultImageUrl = resultImageUrl;
        this.status = status;
        this.predictionId = predictionId;
        this.member = member;
    }

    public void updateResultImageAndStatus(String resultImageUrl, String status) {
        this.resultImageUrl = resultImageUrl;
        this.status = status;
    }

}
