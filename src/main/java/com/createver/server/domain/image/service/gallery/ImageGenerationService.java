package com.createver.server.domain.image.service.gallery;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.createver.server.domain.image.dto.request.ImageGenerationRequest;
import com.createver.server.domain.image.dto.request.PromptRequest;
import com.createver.server.domain.image.dto.response.CustomGenerationResponse;
import com.createver.server.domain.image.dto.response.ImageGenerationResponse;

import com.createver.server.domain.image.entity.Gallery;
import com.createver.server.domain.image.entity.ImageTag;
import com.createver.server.domain.image.service.tag.ImageTagService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ImageGenerationService {

    private final OpenAiService openAiService;
    private final S3UploadService s3UploadService;
    private final GalleryService galleryService;
    private final MemberRepository memberRepository;
    private final ImageTagService imageTagService;
    private final RateLimiterManager rateLimiterManager;
    private final Translate translate;


    /**
     * 사용자의 프롬프트 요청에 따라 이미지를 생성하고, 생성된 이미지를 갤러리에 저장한 후 응답을 반환합니다.
     *
     * @param promptRequest 사용자로부터 받은 이미지 생성 요청
     * @param email         사용자의 이메일 주소
     * @return 생성된 이미지 정보가 담긴 CustomGenerationResponse 객체 리스트
     */
    public List<CustomGenerationResponse> makeImages(PromptRequest promptRequest, String email) {
        validateRateLimit(email);
        String prompt = getTranslatedPrompt(promptRequest.getPrompt());
        List<ImageGenerationResponse.ImageURL> imageUrls = generateImages(
                prompt,
                promptRequest.getOption(),
                OpenAiConfig.MODEL,
                OpenAiConfig.IMAGE_COUNT_1,
                getNextImageSize()
        );
        return saveAndRespond(imageUrls, promptRequest, email);
    }

    /**
     * 간단한 이미지 생성 요청을 처리합니다.
     *
     * @param prompt 사용자로부터 받은 텍스트 프롬프트
     * @return 생성된 이미지의 S3 URL 리스트
     */
    public List<String> simpleImageMake(String prompt) {
        String translatedPrompt = getTranslatedPrompt(prompt);
        List<ImageGenerationResponse.ImageURL> imageUrls = generateImages(
                translatedPrompt,
                0,
                OpenAiConfig.MODEL2,
                OpenAiConfig.IMAGE_COUNT_2,
                OpenAiConfig.FIXED_SIZE_512);
        return uploadImagesToS3(imageUrls);
    }

    private void validateRateLimit(String email) {
        if (!rateLimiterManager.allowRequest(email)) {
            log.error("요청 빈도 제한 초과: {}", email);
            throw new BusinessLogicException(ExceptionCode.RATE_LIMIT_EXCEEDED, "Rate limit exceeded");
        }
    }

    private String getTranslatedPrompt(String prompt) {
        return LanguageDiscriminationUtils.translateIfKorean(prompt, translate);
    }

    private List<ImageGenerationResponse.ImageURL> generateImages(String prompt, int option, String model, int imageCount, String imageSize) {
        try {
            String modifiedPrompt = modifyPromptBasedOnOption(prompt, option);
            ImageGenerationRequest request = ImageGenerationRequest.builder()
                    .prompt(modifiedPrompt)
                    .model(model)
                    .n(imageCount)
                    .size(imageSize)
                    .response_format(OpenAiConfig.RESPONSE_FORMAT)
                    .build();
            return openAiService.generateImage(request).getData();
        } catch (Exception e) {
            log.error("이미지 생성 실패: {}", e.getMessage());
            throw new BusinessLogicException(ExceptionCode.OPENAI_API_ERROR, "Image generation failed: " + e.getMessage());
        }
    }

    private List<CustomGenerationResponse> saveAndRespond(List<ImageGenerationResponse.ImageURL> imageUrls, PromptRequest promptRequest, String email) {
        List<CustomGenerationResponse> responses = new ArrayList<>();
        List<ImageTag> tags = imageTagService.getOrCreateTags(promptRequest.getPrompt().split(" "));
        Member currentMember = email != null ? memberRepository.findByEmail(email).orElse(null) : null;

        imageUrls.forEach(imageUrl -> {
            byte[] decodedImage = Base64.getDecoder().decode(imageUrl.getB64_json());
            String s3Url = uploadImageToS3(decodedImage);
            Gallery savedGallery = galleryService.createGallery(promptRequest.getPrompt(), s3Url, promptRequest.getOption(), tags, currentMember);
            responses.add(buildCustomGenerationResponse(savedGallery, s3Url));
        });

        return responses;
    }

    private CustomGenerationResponse buildCustomGenerationResponse(Gallery savedGallery, String s3Url) {
        return CustomGenerationResponse.builder()
                .galleryId(savedGallery.getGalleryId())
                .prompt(savedGallery.getPrompt())
                .s3Url(s3Url)
                .option(savedGallery.getOption())
                .createdAt(savedGallery.getCreatedAt())
                .build();
    }

    private List<String> uploadImagesToS3(List<ImageGenerationResponse.ImageURL> imageUrls) {
        List<String> s3Urls = new ArrayList<>();
        imageUrls.forEach(imageUrl -> s3Urls.add(uploadImageToS3(Base64.getDecoder().decode(imageUrl.getB64_json()))));
        return s3Urls;
    }

    private String uploadImageToS3(byte[] imageData) {
        try {
            return s3UploadService.uploadAndReturnCloudFrontUrl(imageData, "image/png");
        } catch (AmazonS3Exception e) {
            log.error("S3 업로드 실패: {}", e.getMessage());
            throw new BusinessLogicException(ExceptionCode.S3_UPLOAD_ERROR, "S3 Upload failed: " + e.getMessage());
        }
    }

    private String getNextImageSize() {
        return isLargeSizeNext ? "1024x1792" : "1024x1024";
    }

    private boolean isLargeSizeNext = true;

    private String modifyPromptBasedOnOption(String originalPrompt, int option) {
        return switch (option) {
            case 1 -> "like a work of art " + originalPrompt;
            case 2 -> "like a human drawing " + originalPrompt;
            case 3 -> "like a real photo " + originalPrompt;
            default -> originalPrompt;
        };
    }
}
