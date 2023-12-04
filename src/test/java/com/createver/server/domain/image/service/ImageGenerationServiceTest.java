package com.createver.server.domain.image.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.createver.server.domain.image.dto.request.PromptRequest;
import com.createver.server.domain.image.dto.response.CustomGenerationResponse;
import com.createver.server.domain.image.dto.response.ImageGenerationResponse;
import com.createver.server.domain.image.entity.Gallery;
import com.createver.server.domain.image.entity.ImageTag;
import com.createver.server.domain.image.repository.gallery.GalleryRepository;
import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.repository.MemberRepository;
import com.createver.server.global.error.exception.BusinessLogicException;
import com.createver.server.global.error.exception.ExceptionCode;
import com.createver.server.global.util.aws.service.S3UploadService;
import com.createver.server.global.util.ratelimit.RateLimiterManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class ImageGenerationServiceTest {

    @InjectMocks
    private ImageGenerationService imageGenerationService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private S3UploadService s3UploadService;

    @Mock
    private GalleryRepository galleryRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ImageTagService imageTagService;


    @Mock
    private RateLimiterManager rateLimiterManager;


    @Test
    void testMakeImages() {
        // Given
        String email = "test@test.com";
        PromptRequest promptRequest = new PromptRequest("test", 1);

        Member member = Member.builder()
                .email(email)
                .nickName("nick")
                .password("password").build();

        Gallery gallery = Gallery.builder()
                .prompt("prompt")
                .storageUrl("s3Url")
                .option(1).build();

        ImageTag imageTag = ImageTag.builder()
                .name("tag")
                .build();

        ImageGenerationResponse.ImageURL imageURL = new ImageGenerationResponse.ImageURL();
        imageURL.setB64_json("someBase64EncodedString");
        ImageGenerationResponse imageGenerationResponse = new ImageGenerationResponse();
        imageGenerationResponse.setData(Arrays.asList(imageURL, imageURL, imageURL, imageURL)); // Mocking 4 images

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(rateLimiterManager.allowRequest(email)).thenReturn(true);
        when(restTemplate.postForEntity(anyString(), any(), eq(ImageGenerationResponse.class)))
                .thenReturn(new ResponseEntity<>(imageGenerationResponse, HttpStatus.OK));
        when(s3UploadService.upload(any(), anyString())).thenReturn("s3Url");
        when(galleryRepository.save(any(Gallery.class))).thenReturn(gallery);
        when(imageTagService.getOrCreateTags(any())).thenReturn(Arrays.asList(imageTag));

        // When
        List<CustomGenerationResponse> result = imageGenerationService.makeImages(promptRequest, email);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(4, result.size());
        assertEquals("s3Url", result.get(0).getS3Url());
    }

    @Test
    void testMakeImages_rateLimitExceeded() {
        // Given
        String email = "test@test.com";
        PromptRequest promptRequest = new PromptRequest("test", 1);

        Mockito.when(rateLimiterManager.allowRequest(email)).thenReturn(false);

        BusinessLogicException thrown = assertThrows(
                BusinessLogicException.class,
                () -> imageGenerationService.makeImages(promptRequest, email),
                "Expected makeImages() to throw, but it didn't"
        );

        assertEquals(ExceptionCode.RATE_LIMIT_EXCEEDED, thrown.getExceptionCode());
    }
}
