package com.createver.server.domain.image.dto.response;

import com.createver.server.domain.image.dto.ImageAvatarDto;
import com.createver.server.domain.member.dto.response.MemberResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ImageAvatarResponse {

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
    private final MemberResponse member;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public static ImageAvatarResponse from(ImageAvatarDto dto){
        return new ImageAvatarResponse(
                dto.getAvatarId(),
                dto.getPrompt(),
                dto.getNumSteps(),
                dto.getStyleName(),
                dto.getInputImage(),
                dto.getNumOutputs(),
                dto.getGuidanceScale(),
                dto.getNegativePrompt(),
                dto.getStyleStrengthRatio(),
                dto.getResultImageUrl(),
                dto.getStatus(),
                dto.getPredictionId(),
                MemberResponse.from(dto.getMember()),
                dto.getCreatedAt(),
                dto.getModifiedAt()
        );
    }
}
