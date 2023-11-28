package com.template.server.domain.music.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicGenerationInput implements Serializable {

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
