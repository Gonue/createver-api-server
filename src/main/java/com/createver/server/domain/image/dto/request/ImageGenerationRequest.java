package com.createver.server.domain.image.dto.request;

import lombok.*;

import java.io.Serializable;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageGenerationRequest implements Serializable {
    private String prompt;
    private String model;
    private int n;
    private String size;
    private String response_format;
}
