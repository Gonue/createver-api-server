package com.template.server.domain.image.service;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.template.server.domain.image.dto.request.ImageGenerationRequest;
import com.template.server.domain.image.dto.request.PromptRequest;
import com.template.server.domain.image.dto.request.pro.StableGenerationRequest;
import com.template.server.domain.image.dto.response.CustomGenerationResponse;
import com.template.server.domain.image.dto.response.ImageGenerationResponse;

import com.template.server.domain.image.dto.response.pro.StablePromptRequest;
import com.template.server.domain.image.entity.Gallery;
import com.template.server.domain.image.entity.ImageTag;
import com.template.server.domain.image.repository.gallery.GalleryRepository;
import com.template.server.domain.member.entity.Member;
import com.template.server.domain.member.entity.PlanType;
import com.template.server.domain.member.repository.MemberRepository;
import com.template.server.global.config.OpenAiConfig;
import com.template.server.global.error.exception.BusinessLogicException;
import com.template.server.global.error.exception.ExceptionCode;
import com.template.server.global.util.aws.service.S3UploadService;
import com.template.server.global.util.translate.LanguageDiscriminationUtils;
import com.template.server.global.util.ratelimit.RateLimiterManager;
import com.template.server.global.util.translate.Translate;
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

    public List<CustomGenerationResponse> stableMakeImage(String email, StablePromptRequest promptRequest) {

        Member member = memberRepository.findByEmail(email).orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND, String.format("%s 를 찾을 수 없습니다.", email))
        );

        PlanType currentPlanType = member.getPlan().getPlanType();
        if (currentPlanType != PlanType.PRO && currentPlanType != PlanType.ULTRA) {
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED_ACCESS, "Pro 또는 Ultra 플랜 회원만 이용 가능한 기능입니다.");
        }

        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
            httpHeaders.add("Authorization", "BEARER" + sageMakerKey);

            String originalPrompt = promptRequest.getPrompt();
            String translatedPrompt = originalPrompt;

            if (LanguageDiscriminationUtils.isKorean(originalPrompt)) {
                translatedPrompt = translate.translate(originalPrompt, "ko", "en");
            }

            StableGenerationRequest stableGenerationRequest = StableGenerationRequest.builder()
                    .checkPoint(promptRequest.getCheckPoint())
                    .textInversion(promptRequest.getTextInversion())
                    .lora(promptRequest.getLora())
                    .prompt(translatedPrompt)
                    .width(promptRequest.getWidth())
                    .height(promptRequest.getHeight())
                    .num_images_per_prompt(promptRequest.getNum_images_per_prompt())
                    .num_inference_steps(promptRequest.getNum_inference_steps())
                    .guidance_scale(promptRequest.getGuidance_scale())
                    .seed(promptRequest.getSeed())
                    .option(5)
                    .response_format("b64_json")
                    .build();

            HttpEntity<StableGenerationRequest> requestHttpEntity = new HttpEntity<>(stableGenerationRequest, httpHeaders);

            ResponseEntity<ImageGenerationResponse> responseEntity = restTemplate.postForEntity(
                    sageMakerEndPoint,
                    requestHttpEntity,
                    ImageGenerationResponse.class
            );

            ImageGenerationResponse imageGenerationResponse = responseEntity.getBody();

            List<CustomGenerationResponse> customGenerationResponses = new ArrayList<>();
            if (imageGenerationResponse == null || imageGenerationResponse.getData() == null) {
                throw new BusinessLogicException(ExceptionCode.GENERAL_ERROR, "SageMaker API No Response");
            }

            for (ImageGenerationResponse.ImageURL imageUrl : imageGenerationResponse.getData()) {
                byte[] decodedImage = Base64.getDecoder().decode(imageUrl.getB64_json());
                String s3Url = s3UploadService.upload(decodedImage, "image/png");


                Gallery gallery = Gallery.builder()
                        .prompt(promptRequest.getPrompt())
                        .storageUrl(s3Url)
                        .option(promptRequest.getOption())
                        .member(member).build();

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
            throw new BusinessLogicException(ExceptionCode.SAGEMAKER_API_ERROR, "SageMaker API 호출 실패");
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
