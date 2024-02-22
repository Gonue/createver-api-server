package com.createver.server.domain.image.service.avatar;

import com.createver.server.domain.image.dto.request.AvatarPromptRequest;
import com.createver.server.domain.image.dto.request.ImageAvatarRequest;
import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.repository.MemberRepository;
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
public class ImageAvatarGenerationService {

    private final MemberRepository memberRepository;
    private final TranslateService translateService;
    private final SageMakerApiClient sageMakerService;
    private final ImageAvatarService imageAvatarService;

    /**
     * 사용자의 프롬프트 및 사진 요청에 따라 이미지를 재생성하고, 생성된 이미지를 및 정보를 저장한 후 모델서버에서 받은 id 값을 반환합니다.
     *
     * @param avatarPromptRequest 사용자로부터 받은 이미지 생성 요청
     * @param email               사용자의 이메일 주소
     * @return 생성된 이미지 정보를 가져올수 있는 id값
     */
    public String generateAvatarImage(AvatarPromptRequest avatarPromptRequest, String email) {
        try {
            String translatedPrompt = translateService.translateIfKorean(avatarPromptRequest.getPrompt());

            ImageAvatarRequest imageAvatarRequest = buildImageAvatarRequest(translatedPrompt, avatarPromptRequest);
            String predictionId = sageMakerService.callSageMakerApi(imageAvatarRequest);

            Member member = findMemberByEmail(email);
            imageAvatarService.saveImageAvatarDetails(avatarPromptRequest, predictionId, member);

            return predictionId;
        } catch (BusinessLogicException e) {
            log.error("Business logic error during avatar generation for email {}: {}", email, e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.SAGEMAKER_NO_RESPONSE);
        }
    }

    private ImageAvatarRequest buildImageAvatarRequest(String translatedPrompt, AvatarPromptRequest avatarPromptRequest) {
        translatedPrompt = ensureContainsImgKeyword(translatedPrompt);

        ImageAvatarRequest.Input input = ImageAvatarRequest.Input.builder()
                .prompt(translatedPrompt)
                .numSteps(avatarPromptRequest.getNumSteps())
                .styleName(avatarPromptRequest.getStyleName())
                .inputImage(avatarPromptRequest.getInputImage())
                .numOutputs(avatarPromptRequest.getNumOutputs())
                .guidanceScale(avatarPromptRequest.getGuidanceScale())
                .negativePrompt(avatarPromptRequest.getNegativePrompt())
                .styleStrengthRatio(avatarPromptRequest.getStyleStrengthRatio())
                .build();

        return ImageAvatarRequest.builder()
                .version(SageMakerConfig.VERSION_1)
                .input(input)
                .webhook(SageMakerConfig.WEBHOOK_END_POINT)
                .webhookEventsFilter(List.of(SageMakerConfig.WEBHOOK_EVENT_COMPLETED))
                .build();
    }

    private Member findMemberByEmail(String email) {
        return email != null ? memberRepository.findByEmail(email).orElse(null) : null;
    }

    private String ensureContainsImgKeyword(String prompt) {
        return prompt.toLowerCase().contains("img") ? prompt : prompt + " img";
    }
}
