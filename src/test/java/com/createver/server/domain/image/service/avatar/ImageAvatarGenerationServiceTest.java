package com.createver.server.domain.image.service.avatar;

import com.createver.server.domain.image.dto.request.AvatarPromptRequest;
import com.createver.server.domain.image.dto.request.ImageAvatarRequest;
import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.repository.MemberRepository;
import com.createver.server.global.client.SageMakerApiClient;
import com.createver.server.global.error.exception.BusinessLogicException;
import com.createver.server.global.error.exception.ExceptionCode;
import com.createver.server.global.util.translate.service.TranslateService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@DisplayName("Image Avatar Generation Service 테스트")
@ExtendWith(MockitoExtension.class)
class ImageAvatarGenerationServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TranslateService translateService;

    @Mock
    private SageMakerApiClient sageMakerService;

    @Mock
    private ImageAvatarService imageAvatarService;

    @InjectMocks
    private ImageAvatarGenerationService imageAvatarGenerationService;


    @Test
    @DisplayName("아바타 이미지 생성 성공 테스트")
    void generateAvatarImage_Success() {
        // given
        String prompt = "테스트 프롬프트";
        String email = "test@example.com";
        String expectedPredictionId = "predictionId";

        Member member = Member.builder()
            .email("test@email.com")
            .build();

        AvatarPromptRequest avatarPromptRequest = AvatarPromptRequest.builder()
             .prompt(prompt)
             .numSteps(5)
             .styleName("test style")
             .inputImage("test image")
             .numOutputs(1)
             .guidanceScale(3)
             .negativePrompt("test negative prompt")
             .styleStrengthRatio(25)
             .build();

        when(memberRepository.findByEmail(eq(email))).thenReturn(Optional.of(member));
        when(translateService.translateIfKorean(eq(prompt))).thenReturn("Translated prompt");
        when(sageMakerService.callSageMakerApi(any(ImageAvatarRequest.class))).thenReturn(expectedPredictionId);

        String predictionId = imageAvatarGenerationService.generateAvatarImage(avatarPromptRequest, email);

        // then
        assertNotNull(predictionId);
        assertEquals(expectedPredictionId, predictionId);
    }
    @Test
    @DisplayName("아바타 이미지 생성 실패 - SageMaker API 호출 실패")
    void generateAvatarImage_FailsDueToSageMakerApiException() {
        AvatarPromptRequest avatarPromptRequest = AvatarPromptRequest.builder()
            .prompt("테스트 프롬프트")
            .build();
        String email = "user@example.com";

        when(translateService.translateIfKorean(anyString())).thenReturn("Translated prompt");
        when(sageMakerService.callSageMakerApi(any(ImageAvatarRequest.class)))
            .thenThrow(new BusinessLogicException(ExceptionCode.SAGEMAKER_NO_RESPONSE));

        assertThrows(BusinessLogicException.class, () -> imageAvatarGenerationService.generateAvatarImage(avatarPromptRequest, email));
    }

}