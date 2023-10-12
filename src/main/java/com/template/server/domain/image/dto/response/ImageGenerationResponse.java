package com.template.server.domain.image.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ImageGenerationResponse {

    private long created;
    private List<ImageURL> data;

    @Getter
    @Setter
    public static class ImageURL{
        private String url;
        private String b64_json;
    }
}
