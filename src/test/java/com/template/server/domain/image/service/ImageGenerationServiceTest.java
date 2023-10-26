package com.template.server.domain.image.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.template.server.domain.image.dto.request.PromptRequest;
import com.template.server.domain.image.dto.response.CustomGenerationResponse;
import com.template.server.domain.image.dto.response.ImageGenerationResponse;
import com.template.server.domain.image.entity.Gallery;
import com.template.server.domain.image.entity.ImageTag;
import com.template.server.domain.image.repository.GalleryRepository;
import com.template.server.domain.image.service.ImageGenerationService;
import com.template.server.domain.image.service.ImageTagService;
import com.template.server.domain.image.service.S3UploadService;
import com.template.server.domain.member.entity.Member;
import com.template.server.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ImageGenerationServiceTest {

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testMakeImages() {
        // Given
        String email = "test@test.com";
        PromptRequest promptRequest = new PromptRequest("test", 1);

        Member member = Member.of(email, "nickname", "password");
        Gallery gallery = Gallery.create("prompt", "s3Url", 1);
        ImageTag imageTag = ImageTag.create("tag");

        ImageGenerationResponse.ImageURL imageURL = new ImageGenerationResponse.ImageURL();
        imageURL.setB64_json("someBase64EncodedString");
        ImageGenerationResponse imageGenerationResponse = new ImageGenerationResponse();
        imageGenerationResponse.setData(Arrays.asList(imageURL, imageURL, imageURL, imageURL)); // Mocking 4 images

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
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
}
