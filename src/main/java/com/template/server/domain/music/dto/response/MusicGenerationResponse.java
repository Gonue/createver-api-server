package com.template.server.domain.music.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MusicGenerationResponse {

    private String id;
    private String model;
    private String version;
    private Map<String, Object> input;
    private String logs;
    private String output;
    private String error;
    private String status;
    private String createdAt;
    private String startedAt;
    private String completedAt;
    private Map<String, Object> metrics;
    private Map<String, String> urls;
}
