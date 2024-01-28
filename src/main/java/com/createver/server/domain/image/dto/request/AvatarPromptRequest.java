package com.createver.server.domain.image.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvatarPromptRequest implements Serializable {
    private String prompt;
    private Integer numSteps;
    private String styleName;
    private String inputImage;
    private Integer numOutputs;
    private Integer guidanceScale;
    private String negativePrompt;
    private Integer styleStrengthRatio;
}
