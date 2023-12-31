package com.createver.server.domain.image.dto.response.pro;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StablePromptRequest {
    private int checkPoint;
    private int textInversion;
    private int lora;
    @NotBlank
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
