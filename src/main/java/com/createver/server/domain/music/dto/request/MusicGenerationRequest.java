package com.createver.server.domain.music.dto.request;

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
public class MusicGenerationRequest implements Serializable {

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

        @JsonProperty("top_k")
        private int topK;
        @JsonProperty("top_p")
        private int topP;
        private String prompt;
        private int duration;
        private int temperature;
        private boolean continuation;
        @JsonProperty("model_version")
        private String modelVersion;
        @JsonProperty("output_format")
        private String outputFormat;
        @JsonProperty("continuation_start")
        private int continuationStart;
        @JsonProperty("multi_band_diffusion")
        private boolean multiBandDiffusion;
        @JsonProperty("normalization_strategy")
        private String normalizationStrategy;
        @JsonProperty("classifier_free_guidance")
        private int classifierFreeGuidance;

    }
}
