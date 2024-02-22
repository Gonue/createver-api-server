package com.createver.server.domain.image.service.gallery;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.createver.server.domain.image.dto.request.ImageGenerationRequest;
import com.createver.server.domain.image.dto.request.PromptRequest;
import com.createver.server.domain.image.dto.response.CustomGenerationResponse;
import com.createver.server.domain.image.dto.response.ImageGenerationResponse;
import com.createver.server.domain.image.entity.Gallery;
import com.createver.server.domain.image.entity.ImageTag;
import com.createver.server.global.client.OpenAiApiClient;
import com.createver.server.domain.image.service.tag.ImageTagService;
import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.repository.MemberRepository;
import com.createver.server.global.error.exception.BusinessLogicException;
import com.createver.server.global.error.exception.ExceptionCode;
import com.createver.server.global.util.aws.service.S3UploadService;
import com.createver.server.global.util.ratelimit.RateLimiterManager;
import com.createver.server.global.util.translate.service.TranslateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;


@DisplayName("Image Generation Service 테스트")
@ExtendWith(MockitoExtension.class)
class ImageGenerationServiceTest {

    @InjectMocks
    private ImageGenerationService imageGenerationService;

    @Mock
    private OpenAiApiClient openAiApiClient;

    @Mock
    private S3UploadService s3UploadService;
    @Mock
    private TranslateService translateService;
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private GalleryService galleryService;
    @Mock
    private ImageTagService imageTagService;

    @Mock
    private RateLimiterManager rateLimiterManager;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("기본 이미지 생성 테스트")
    void testMakeImages() {
        String email = "test@test.com";
        PromptRequest promptRequest = new PromptRequest("test prompt", 1);
        Member member = Member.builder().email(email).build();
        ImageTag imageTag = ImageTag.builder().build();

        ImageGenerationResponse.ImageURL imageURL = new ImageGenerationResponse.ImageURL();
        String originalInput = "Test String";
        String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
        imageURL.setB64_json(encodedString);
        ImageGenerationResponse imageGenerationResponse = new ImageGenerationResponse();
        imageGenerationResponse.setData(Arrays.asList(imageURL));

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(openAiApiClient.generateImage(any(ImageGenerationRequest.class))).thenReturn(imageGenerationResponse);
        when(s3UploadService.uploadAndReturnCloudFrontUrl(any(byte[].class), anyString())).thenReturn("s3Url");
        when(imageTagService.getOrCreateTags(any(String[].class))).thenReturn(Collections.singletonList(imageTag));
        when(rateLimiterManager.allowRequest(email)).thenReturn(true);
        when(galleryService.createGallery(anyString(), anyString(), anyInt(), anyList(), any(Member.class)))
                .thenReturn(Gallery.builder().prompt(promptRequest.getPrompt()).storageUrl("s3Url").option(promptRequest.getOption()).tags(Arrays.asList(imageTag)).member(member).build());

        List<CustomGenerationResponse> responses = imageGenerationService.makeImages(promptRequest, email);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("s3Url", responses.get(0).getS3Url());
    }

    @Test
    @DisplayName("단순 이미지 생성 테스트")
    void testSimpleImageMake() {
        String prompt = "test prompt";

        String encodedString = Base64.getEncoder().encodeToString("test image data".getBytes());
        ImageGenerationResponse.ImageURL imageURL = new ImageGenerationResponse.ImageURL();
        imageURL.setB64_json(encodedString);
        ImageGenerationResponse imageGenerationResponse = new ImageGenerationResponse();
        imageGenerationResponse.setData(Arrays.asList(imageURL, imageURL));

        when(openAiApiClient.generateImage(any(ImageGenerationRequest.class))).thenReturn(imageGenerationResponse);
        when(s3UploadService.uploadAndReturnCloudFrontUrl(any(byte[].class), eq("image/png"))).thenReturn("s3Url");
        when(translateService.translateIfKorean(anyString())).thenReturn(prompt);

        List<String> s3Urls = imageGenerationService.simpleImageMake(prompt);

        assertNotNull(s3Urls);
        assertEquals(2, s3Urls.size());
        assertTrue(s3Urls.stream().allMatch(url -> url.equals("s3Url")));
    }

    @Test
    @DisplayName("이미지 생성 요청 제한 테스트")
    void testMakeImages_rateLimitExceeded() {
        String email = "test@test.com";
        PromptRequest promptRequest = new PromptRequest("test prompt", 1);

        when(rateLimiterManager.allowRequest(any(String.class))).thenReturn(false);

        BusinessLogicException exception = assertThrows(
                BusinessLogicException.class,
                () -> imageGenerationService.makeImages(promptRequest, email)
        );

        assertEquals(ExceptionCode.RATE_LIMIT_EXCEEDED, exception.getExceptionCode());
    }

    @Test
    @DisplayName("OpenAI 서비스 호출 실패 시 OPENAI_API_ERROR 예외 발생")
    void testImageGenerationFailure() {
        // Given
        PromptRequest promptRequest = new PromptRequest("test prompt", 1);
        String email = "user@example.com";

        when(rateLimiterManager.allowRequest(email)).thenReturn(true);
        doThrow(new BusinessLogicException(ExceptionCode.OPENAI_API_ERROR, "OpenAI API 호출 실패"))
                .when(openAiApiClient).generateImage(any(ImageGenerationRequest.class));

        // When & Then
        BusinessLogicException exception = assertThrows(
                BusinessLogicException.class,
                () -> imageGenerationService.makeImages(promptRequest, email)
        );

        assertEquals(ExceptionCode.OPENAI_API_ERROR, exception.getExceptionCode());
    }
}
