package com.createver.server.domain.image.service.avatar;

import com.createver.server.domain.image.dto.request.AvatarPromptRequest;
import com.createver.server.domain.image.entity.ImageAvatar;
import com.createver.server.domain.image.repository.avatar.ImageAvatarRepository;
import com.createver.server.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Image Avatar Service 테스트")
@ExtendWith(MockitoExtension.class)
class ImageAvatarServiceTest {

    @InjectMocks
    private ImageAvatarService imageAvatarService;

    @Mock
    private ImageAvatarRepository imageAvatarRepository;

    @DisplayName("이미지 아바타 세부 정보 저장 성공 테스트")
    @Test
    void saveImageAvatarDetails_Success() {
        // Given
        AvatarPromptRequest request = AvatarPromptRequest.builder()
                .prompt("test prompt")
                .numSteps(5)
                .styleName("test style")
                .inputImage("test image")
                .numOutputs(1)
                .guidanceScale(3)
                .negativePrompt("test negative prompt")
                .styleStrengthRatio(25)
                .build();
        String predictionId = "predictionId";
        Member testMember = Member.builder()
                .email("test@email.com")
                .build();

        // When
        imageAvatarService.saveImageAvatarDetails(request, predictionId, testMember);

        // Then
        verify(imageAvatarRepository, times(1)).save(any(ImageAvatar.class));
    }
}