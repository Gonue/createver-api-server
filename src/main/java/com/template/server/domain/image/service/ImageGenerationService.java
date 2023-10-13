package com.template.server.domain.image.service;

import com.template.server.domain.image.dto.request.ImageGenerationRequest;
import com.template.server.domain.image.dto.request.PromptRequest;
import com.template.server.domain.image.dto.response.CustomGenerationResponse;
import com.template.server.domain.image.dto.response.ImageGenerationResponse;

import com.template.server.domain.image.entity.Gallery;
import com.template.server.domain.image.repository.GalleryRepository;
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
    private final GalleryRepository galleryRepository;

    @Value("${openai.api-key}")
    private String apiKey;

    public CustomGenerationResponse makeImages(PromptRequest promptRequest){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.parseMediaType(OpenAiConfig.MEDIA_TYPE));
        httpHeaders.add(OpenAiConfig.AUTHORIZATION, OpenAiConfig.BEARER + apiKey);

        String modifiedPrompt = modifyPromptBasedOnOption(promptRequest.getPrompt(), promptRequest.getOption());

        ImageGenerationRequest imageGenerationRequest = new ImageGenerationRequest();
        imageGenerationRequest.setPrompt(modifiedPrompt);
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

        Gallery gallery = Gallery.create(promptRequest.getPrompt(), s3Url, promptRequest.getOption());
        galleryRepository.save(gallery);

        CustomGenerationResponse customGenerationResponse = new CustomGenerationResponse();
        customGenerationResponse.setS3Url(s3Url);

        return customGenerationResponse;
    }

    private String modifyPromptBasedOnOption(String originalPrompt, int option) {
        String prefix = "";
        switch (option) {
            case 1:
                prefix = "like a work of art ";
                break;
            case 2:
                prefix = "like a human drawing ";
                break;
            case 3:
                prefix = "like a real photo ";
                break;
            default:
                prefix = "";
                break;
        }
        return prefix + originalPrompt;
    }
}
