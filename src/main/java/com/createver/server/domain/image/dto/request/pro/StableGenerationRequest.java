package com.createver.server.domain.image.dto.request.pro;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StableGenerationRequest implements Serializable {
    private int checkPoint;
    private int textInversion;
    private int lora;
    private String prompt;
    private int width;
    private int height;
    private int num_images_per_prompt;
    private int num_inference_steps;
    private double guidance_scale;
    private int seed;
    private int option;
    private String response_format;
}
