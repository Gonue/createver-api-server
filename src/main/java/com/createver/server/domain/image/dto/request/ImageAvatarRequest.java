package com.createver.server.domain.image.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageAvatarRequest implements Serializable {
    private String version;
    private Input input;
    private String webhook;

    @JsonProperty("webhook_events_filter")
    private List<String> webhookEventsFilter;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Input implements Serializable {
        private String prompt;

        @JsonProperty("num_steps")
        private Integer numSteps;

        @JsonProperty("style_name")
        private String styleName;

        @JsonProperty("input_image")
        private String inputImage;

        @JsonProperty("num_outputs")
        private Integer numOutputs;

        @JsonProperty("guidance_scale")
        private Integer guidanceScale;

        @JsonProperty("negative_prompt")
        private String negativePrompt;

        @JsonProperty("style_strength_ratio")
        private Integer styleStrengthRatio;
    }
}
