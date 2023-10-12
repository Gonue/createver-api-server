package com.template.server.domain.image.service;

import com.template.server.domain.image.dto.request.ImageGenerationRequest;
import com.template.server.domain.image.dto.request.PromptRequest;
import com.template.server.domain.image.dto.response.CustomGenerationResponse;
import com.template.server.domain.image.dto.response.ImageGenerationResponse;

import com.template.server.global.config.OpenAiConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;


@Service
@RequiredArgsConstructor
public class ImageGenerationService {

    private final RestTemplate restTemplate;
    private final S3UploadService s3UploadService;
    @Value("${openai.api-key}")
    private String apiKey;

    public CustomGenerationResponse makeImages(PromptRequest promptRequest){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.parseMediaType(OpenAiConfig.MEDIA_TYPE));
        httpHeaders.add(OpenAiConfig.AUTHORIZATION, OpenAiConfig.BEARER + apiKey);

        ImageGenerationRequest imageGenerationRequest = new ImageGenerationRequest();
        imageGenerationRequest.setPrompt(promptRequest.getPrompt());
        imageGenerationRequest.setN(OpenAiConfig.IMAGE_COUNT);
        imageGenerationRequest.setSize(OpenAiConfig.IMAGE_SIZE);
        imageGenerationRequest.setResponse_format(OpenAiConfig.RESPONSE_FORMAT);

        HttpEntity<ImageGenerationRequest> requestHttpEntity = new HttpEntity<>(imageGenerationRequest, httpHeaders);

        ResponseEntity<ImageGenerationResponse> responseEntity = restTemplate.postForEntity(
                OpenAiConfig.IMAGE_URL,
                requestHttpEntity,
                ImageGenerationResponse.class
        );

        ImageGenerationResponse imageGenerationResponse = responseEntity.getBody();

        String base64Data = imageGenerationResponse.getData().get(0).getB64_json();
        byte[] decodedImage = Base64.getDecoder().decode(base64Data);
        String s3Url = s3UploadService.upload(decodedImage, "image/png");


        CustomGenerationResponse customGenerationResponse = new CustomGenerationResponse();
        customGenerationResponse.setS3Url(s3Url);

        return customGenerationResponse;
    }
}
