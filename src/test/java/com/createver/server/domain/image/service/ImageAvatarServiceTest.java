package com.createver.server.domain.image.service;

import com.createver.server.domain.image.dto.request.AvatarPromptRequest;
import com.createver.server.domain.image.entity.ImageAvatar;
import com.createver.server.domain.image.repository.avatar.ImageAvatarRepository;
import com.createver.server.domain.image.service.avatar.ImageAvatarService;
import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Image Avatar Generation Service 테스트")
@ExtendWith(MockitoExtension.class)
class ImageAvatarServiceTest {

    @InjectMocks
    private ImageAvatarService imageAvatarService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ImageAvatarRepository imageAvatarRepository;

    @Mock
    private MemberRepository memberRepository;

    private String sageMakerEndPoint;

    @DisplayName("이미지 아바타 생성 및 저장 성공 테스트")
    @Test
    void generateAvatarImage_Success() {
        // Given
        AvatarPromptRequest request = new AvatarPromptRequest();
        Map<String, Object> apiResponse = new HashMap<>();
        apiResponse.put("id", "predictionId");


        Member testMember = Member.builder()
            .email("test@email.com")
            .nickName("TestUser")
            .password("testPassword")
            .build();

        ImageAvatar mockImageAvatar = ImageAvatar.builder()
            .prompt("test prompt")
            .numSteps(5)
            .styleName("test style")
            .inputImage("test image")
            .numOutputs(1)
            .guidanceScale(3)
            .negativePrompt("test negative prompt")
            .styleStrengthRatio(2)
            .predictionId("predictionId")
            .member(testMember)
            .status("processing")
            .build();

        when(restTemplate.postForEntity(eq(sageMakerEndPoint), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(ResponseEntity.ok(apiResponse));
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(testMember));
        when(imageAvatarRepository.save(any(ImageAvatar.class))).thenReturn(mockImageAvatar);

        // When
        String predictionId = imageAvatarService.generateAvatarImage(request, testMember.getEmail());

        // Then
        assertNotNull(predictionId);
        assertEquals("predictionId", predictionId);
        verify(imageAvatarRepository, times(1)).save(any(ImageAvatar.class));
    }

    @DisplayName("API 응답이 null이거나 id가 없는 경우 예외 발생 테스트")
    @Test
    void generateAvatarImage_ApiResponseNull_ThrowsException() {
        // Given
        AvatarPromptRequest request = new AvatarPromptRequest();

        when(restTemplate.postForEntity(eq(sageMakerEndPoint), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(ResponseEntity.ok(null));

        // When & Then
        assertThrows(RuntimeException.class, () -> imageAvatarService.generateAvatarImage(request, "test@email.com"));
    }
}