package com.template.server.domain.image.dto.request;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageGenerationRequest implements Serializable {

    private String prompt;
    private int n;
    private String size;
    private String response_format;

}
