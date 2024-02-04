package com.createver.server.domain.image.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    private String prompt;

    @Min(1)
    @Max(100)
    private Integer numSteps;

    private String styleName;
    private String inputImage;

    @Min(1)
    @Max(4)
    private Integer numOutputs;

    @Min(1)
    @Max(10)
    private Integer guidanceScale;
    private String negativePrompt;

    @Min(15)
    @Max(50)
    private Integer styleStrengthRatio;
}
