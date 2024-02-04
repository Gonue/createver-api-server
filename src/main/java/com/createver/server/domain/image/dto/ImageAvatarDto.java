package com.createver.server.domain.image.dto;

import com.createver.server.domain.image.entity.ImageAvatar;
import com.createver.server.domain.member.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ImageAvatarDto {

    private final Long avatarId;
    private final String prompt;
    private final Integer numSteps;
    private final String styleName;
    private final String inputImage;
    private final Integer numOutputs;
    private final Integer guidanceScale;
    private final String negativePrompt;
    private final Integer styleStrengthRatio;
    private final String resultImageUrl;
    private final String status;
    private final String predictionId;
    private final MemberDto member;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public static ImageAvatarDto from(ImageAvatar entity){
        return new ImageAvatarDto(
                entity.getAvatarId(),
                entity.getPrompt(),
                entity.getNumSteps(),
                entity.getStyleName(),
                entity.getInputImage(),
                entity.getNumOutputs(),
                entity.getGuidanceScale(),
                entity.getNegativePrompt(),
                entity.getStyleStrengthRatio(),
                entity.getResultImageUrl(),
                entity.getStatus(),
                entity.getPredictionId(),
                MemberDto.from(entity.getMember()),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }
}
