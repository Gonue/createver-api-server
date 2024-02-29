package com.createver.server.domain.music.service;

import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.repository.MemberRepository;
import com.createver.server.domain.music.dto.request.MusicGenerationRequest;
import com.createver.server.domain.music.dto.request.MusicPromptRequest;
import com.createver.server.global.client.SageMakerApiClient;
import com.createver.server.global.config.SageMakerConfig;
import com.createver.server.global.error.exception.BusinessLogicException;
import com.createver.server.global.error.exception.ExceptionCode;
import com.createver.server.global.util.translate.service.TranslateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MusicGenerationService {

    private final MemberRepository memberRepository;
    private final TranslateService translateService;
    private final SageMakerApiClient sageMakerApiClient;
    private final MusicService musicService;

    public String generateMusic(MusicPromptRequest musicPromptRequest, String email) {
        try{
            String translatedPrompt = translateService.translateIfKorean(musicPromptRequest.getPrompt());
            MusicGenerationRequest musicGenerationRequest = buildMusicGenerationRequest(translatedPrompt);

            String predictionId = sageMakerApiClient.callSageMakerApi(musicGenerationRequest);

            Member member = findMemberByEmail(email);
            musicService.saveMusicDetails(musicPromptRequest.getPrompt(), predictionId, member);

            return predictionId;

        } catch (BusinessLogicException e) {
            log.error("Business logic error during avatar generation for email {}: {}", email, e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.SAGEMAKER_NO_RESPONSE);
        }
    }

    private MusicGenerationRequest buildMusicGenerationRequest(String translatedPrompt){

        MusicGenerationRequest.Input input = MusicGenerationRequest.Input.builder()
                .topK(250)
                .topP(0)
                .prompt(translatedPrompt)
                .duration(5)
                .temperature(1)
                .continuation(false)
                .modelVersion("stereo-large")
                .outputFormat("wav")
                .continuationStart(0)
                .multiBandDiffusion(false)
                .normalizationStrategy("peak")
                .classifierFreeGuidance(3)
                .build();

        return MusicGenerationRequest.builder()
                .version(SageMakerConfig.VERSION_2)
                .input(input)
                .webhook(SageMakerConfig.WEBHOOK_END_POINT_MUSIC)
                .webhookEventsFilter(List.of(SageMakerConfig.WEBHOOK_EVENT_COMPLETED))
                .build();
    }

    private Member findMemberByEmail(String email) {
        return email != null ? memberRepository.findByEmail(email).orElse(null) : null;
    }
}
