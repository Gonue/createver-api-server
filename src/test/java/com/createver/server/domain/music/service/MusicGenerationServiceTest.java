package com.createver.server.domain.music.service;

import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.repository.MemberRepository;
import com.createver.server.domain.music.dto.request.MusicGenerationRequest;
import com.createver.server.domain.music.dto.request.MusicPromptRequest;
import com.createver.server.global.client.SageMakerApiClient;
import com.createver.server.global.error.exception.BusinessLogicException;
import com.createver.server.global.error.exception.ExceptionCode;
import com.createver.server.global.util.aws.service.TranslateService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Music Generation Service 테스트")
@ExtendWith(MockitoExtension.class)
class MusicGenerationServiceTest {

    @InjectMocks
    private MusicGenerationService musicGenerationService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TranslateService translateService;

    @Mock
    private SageMakerApiClient sageMakerApiClient;

    @Mock
    private MusicService musicService;

    @DisplayName("음악 생성 성공 테스트")
    @Test
    void generateMusic_Success() {
        // Given

        String prompt = "테스트 프롬프트";
        String email = "test@example.com";
        String expectedPredictionId = "predictionId";

        Member member = Member.builder()
                .email(email)
                .build();

        MusicPromptRequest musicPromptRequest = MusicPromptRequest.builder()
                .prompt(prompt)
                .build();

        when(memberRepository.findByEmail(eq(email))).thenReturn(Optional.of(member));
        when(translateService.translateIfKorean(eq(prompt))).thenReturn("Translated prompt");
        when(sageMakerApiClient.callSageMakerApi(any(MusicGenerationRequest.class))).thenReturn(expectedPredictionId);

        String predictionId = musicGenerationService.generateMusic(musicPromptRequest, email);

        // Then
        assertNotNull(predictionId);
        assertEquals(expectedPredictionId, predictionId);
    }

    @Test
    @DisplayName("아바타 이미지 생성 실패 - SageMaker API 호출 실패")
    void generateMusic_FailsDueToSageMakerApiException() {
        MusicPromptRequest musicPromptRequest = MusicPromptRequest.builder()
                .prompt("테스트 프롬프트")
                .build();
        String email = "user@example.com";

        when(translateService.translateIfKorean(anyString())).thenReturn("Translated prompt");
        when(sageMakerApiClient.callSageMakerApi(any(MusicGenerationRequest.class)))
            .thenThrow(new BusinessLogicException(ExceptionCode.SAGEMAKER_NO_RESPONSE));

        assertThrows(BusinessLogicException.class, () -> musicGenerationService.generateMusic(musicPromptRequest, email));
    }
}