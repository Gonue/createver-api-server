package com.template.server.global.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAiConfig {
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String MEDIA_TYPE = "application/json; charset=UTF-8";
    public static final String IMAGE_URL = "https://api.openai.com/v1/images/generations";
    public static final int IMAGE_COUNT = 4;
    public static final String RESPONSE_FORMAT = "b64_json";
    public static final String IMAGE_SIZE = "1024x1024"; // '256x256', '512x512', '1024x1024'
}
