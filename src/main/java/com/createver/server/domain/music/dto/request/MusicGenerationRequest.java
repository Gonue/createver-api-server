package com.createver.server.domain.music.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicGenerationRequest {

    private String version;
    private MusicGenerationInput input;
}
