package com.createver.server.domain.image.service;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.createver.server.domain.image.dto.request.ImageGenerationRequest;
import com.createver.server.domain.image.dto.request.PromptRequest;
import com.createver.server.domain.image.dto.response.CustomGenerationResponse;
import com.createver.server.domain.image.dto.response.ImageGenerationResponse;

import com.createver.server.domain.image.entity.Gallery;
import com.createver.server.domain.image.entity.ImageTag;
import com.createver.server.domain.image.repository.gallery.GalleryRepository;
import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.repository.MemberRepository;
import com.createver.server.global.config.OpenAiConfig;
import com.createver.server.global.error.exception.BusinessLogicException;
import com.createver.server.global.error.exception.ExceptionCode;
import com.createver.server.global.util.aws.service.S3UploadService;
import com.createver.server.global.util.translate.LanguageDiscriminationUtils;
import com.createver.server.global.util.ratelimit.RateLimiterManager;
import com.createver.server.global.util.translate.Translate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ImageGenerationService {

    private boolean isLargeSizeNext = true;

    private final RestTemplate restTemplate;
    private final S3UploadService s3UploadService;
    private final GalleryRepository galleryRepository;
    private final MemberRepository memberRepository;
    private final ImageTagService imageTagService;
    private final RateLimiterManager rateLimiterManager;
    private final Translate translate;

    @Value("${openai.api-key}")
    private String apiKey;

    @Value(("${sagemaker.api-key}"))
    private String sageMakerKey;
    @Value(("${sagemaker.end-point}"))
    private String sageMakerEndPoint;

    private String getNextImageSize() {
        if (isLargeSizeNext) {
            isLargeSizeNext = false;
            return "1024x1792";
        } else {
            isLargeSizeNext = true;
            return "1024x1024";
        }
    }

    public List<CustomGenerationResponse> makeImages(PromptRequest promptRequest, String email) {
        if (!rateLimiterManager.allowRequest(email)) {
            throw new BusinessLogicException(ExceptionCode.RATE_LIMIT_EXCEEDED, "Rate limit exceeded");
        }
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType(OpenAiConfig.MEDIA_TYPE));
            httpHeaders.add(OpenAiConfig.AUTHORIZATION, OpenAiConfig.BEARER + apiKey);


            String originalPrompt = promptRequest.getPrompt();
            String translatedPrompt = originalPrompt;

            if (LanguageDiscriminationUtils.isKorean(originalPrompt)) {
                translatedPrompt = translate.translate(originalPrompt, "ko", "en");
            }

            String modifiedPrompt = modifyPromptBasedOnOption(translatedPrompt, promptRequest.getOption());

            String imageSize = getNextImageSize();

            ImageGenerationRequest imageGenerationRequest = ImageGenerationRequest.builder()
                    .prompt(modifiedPrompt)
                    .model(OpenAiConfig.MODEL)
                    .n(OpenAiConfig.IMAGE_COUNT)
                    .size(imageSize)
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

            String[] tagNames = promptRequest.getPrompt().split(" ");
            List<ImageTag> tags = imageTagService.getOrCreateTags(tagNames);


            Member currentMember = null;
            if (email != null) {
                currentMember = memberRepository.findByEmail(email).orElse(null);
            }


            for (ImageGenerationResponse.ImageURL imageUrl : imageGenerationResponse.getData()) {
                byte[] decodedImage = Base64.getDecoder().decode(imageUrl.getB64_json());
                String s3Url = s3UploadService.upload(decodedImage, "image/png");

                Gallery.GalleryBuilder galleryBuilder = Gallery.builder()
                        .prompt(promptRequest.getPrompt())
                        .storageUrl(s3Url)
                        .option(promptRequest.getOption())
                        .tags(tags);
                if (currentMember != null) {
                    galleryBuilder.member(currentMember);
                }

                Gallery savedGallery = galleryRepository.save(galleryBuilder.build());

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

    public List<String> simpleImageMake(String prompt) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType(OpenAiConfig.MEDIA_TYPE));
            httpHeaders.add(OpenAiConfig.AUTHORIZATION, OpenAiConfig.BEARER + apiKey);

            String translatedPrompt = prompt;

            if (LanguageDiscriminationUtils.isKorean(prompt)) {
                translatedPrompt = translate.translate(prompt, "ko", "en");
            }

            ImageGenerationRequest imageGenerationRequest = ImageGenerationRequest.builder()
                    .prompt(translatedPrompt)
                    .model(OpenAiConfig.MODEL2)
                    .n(2)
                    .size("512x512")
                    .response_format(OpenAiConfig.RESPONSE_FORMAT)
                    .build();

            HttpEntity<ImageGenerationRequest> requestHttpEntity = new HttpEntity<>(imageGenerationRequest, httpHeaders);

            ResponseEntity<ImageGenerationResponse> responseEntity = restTemplate.postForEntity(
                    OpenAiConfig.IMAGE_URL,
                    requestHttpEntity,
                    ImageGenerationResponse.class
            );

            ImageGenerationResponse imageGenerationResponse = responseEntity.getBody();
            if (imageGenerationResponse == null || imageGenerationResponse.getData() == null) {
                throw new BusinessLogicException(ExceptionCode.GENERAL_ERROR, "OpenAI API No Response");
            }

            List<String> s3Urls = new ArrayList<>();
            for (ImageGenerationResponse.ImageURL imageUrl : imageGenerationResponse.getData()) {
                String base64Image = imageUrl.getB64_json();
                byte[] decodedImage = Base64.getDecoder().decode(base64Image);
                String s3Url = s3UploadService.uploadAndReturnCloudFrontUrl(decodedImage, "image/png");
                s3Urls.add(s3Url);
            }

            return s3Urls;

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
