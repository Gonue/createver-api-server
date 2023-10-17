package com.template.server.domain.image.service;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.template.server.domain.image.dto.request.ImageGenerationRequest;
import com.template.server.domain.image.dto.request.PromptRequest;
import com.template.server.domain.image.dto.response.CustomGenerationResponse;
import com.template.server.domain.image.dto.response.ImageGenerationResponse;

import com.template.server.domain.image.entity.Gallery;
import com.template.server.domain.image.repository.GalleryRepository;
import com.template.server.global.config.OpenAiConfig;
import com.template.server.global.error.exception.BusinessLogicException;
import com.template.server.global.error.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.http.HttpHeaders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class ImageGenerationService {

    private final RestTemplate restTemplate;
    private final S3UploadService s3UploadService;
    private final GalleryRepository galleryRepository;

    @Value("${openai.api-key}")
    private String apiKey;

    public List<CustomGenerationResponse> makeImages(PromptRequest promptRequest) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType(OpenAiConfig.MEDIA_TYPE));
            httpHeaders.add(OpenAiConfig.AUTHORIZATION, OpenAiConfig.BEARER + apiKey);

            String modifiedPrompt = modifyPromptBasedOnOption(promptRequest.getPrompt(), promptRequest.getOption());

            ImageGenerationRequest imageGenerationRequest = ImageGenerationRequest.builder()
                    .prompt(modifiedPrompt)
                    .n(OpenAiConfig.IMAGE_COUNT)
                    .size(OpenAiConfig.IMAGE_SIZE)
                    .response_format(OpenAiConfig.RESPONSE_FORMAT)
                    .build();

            HttpEntity<ImageGenerationRequest> requestHttpEntity = new HttpEntity<>(imageGenerationRequest, httpHeaders);

            ResponseEntity<ImageGenerationResponse> responseEntity = restTemplate.postForEntity(
                    OpenAiConfig.IMAGE_URL,
                    requestHttpEntity,
                    ImageGenerationResponse.class
            );

            ImageGenerationResponse imageGenerationResponse = responseEntity.getBody();

            List<CustomGenerationResponse> customGenerationResponses = new ArrayList<>();
            if (imageGenerationResponse == null || imageGenerationResponse.getData() == null) {
                throw new BusinessLogicException(ExceptionCode.GENERAL_ERROR, "OpenAI API No Response");
            }
            for (ImageGenerationResponse.ImageURL imageUrl : imageGenerationResponse.getData()) {
                byte[] decodedImage = Base64.getDecoder().decode(imageUrl.getB64_json());
                String s3Url = s3UploadService.upload(decodedImage, "image/png");

                Gallery gallery = Gallery.create(promptRequest.getPrompt(), s3Url, promptRequest.getOption());
                Gallery savedGallery = galleryRepository.save(gallery);
                CustomGenerationResponse customGenerationResponse = CustomGenerationResponse.builder()
                        .galleryId(savedGallery.getGalleryId())
                        .prompt(savedGallery.getPrompt())
                        .s3Url(s3Url)
                        .option(savedGallery.getOption())
                        .createdAt(savedGallery.getCreatedAt())
                        .build();
                customGenerationResponses.add(customGenerationResponse);
            }

            return customGenerationResponses;

        } catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerEx) {
            throw new BusinessLogicException(ExceptionCode.OPENAI_API_ERROR, "OpenAI API 호출 실패");
        } catch (AmazonS3Exception s3Ex) {
            throw new BusinessLogicException(ExceptionCode.S3_UPLOAD_ERROR, "S3 업로드 실패");
        } catch (Exception ex) {
            throw new BusinessLogicException(ExceptionCode.GENERAL_ERROR, "이미지 생성 중 오류 발생");
        }
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
