package com.createver.server.domain.image.service.avatar;

import com.createver.server.domain.image.dto.request.AvatarPromptRequest;
import com.createver.server.domain.image.dto.request.ImageAvatarRequest;
import com.createver.server.domain.image.entity.ImageAvatar;
import com.createver.server.domain.image.repository.avatar.ImageAvatarRepository;
import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.repository.MemberRepository;
import com.createver.server.global.util.translate.LanguageDiscriminationUtils;
import com.createver.server.global.util.translate.Translate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageAvatarService {

    private final ImageAvatarRepository imageAvatarRepository;
    private final MemberRepository memberRepository;
    private final Translate translate;
    private final SageMakerService sageMakerService;

    public String generateAvatarImage(AvatarPromptRequest avatarPromptRequest, String email) {
        String translatedPrompt = getTranslatedPrompt(avatarPromptRequest.getPrompt());

        ImageAvatarRequest imageAvatarRequest = buildImageAvatarRequest(translatedPrompt, avatarPromptRequest);
        String predictionId = sageMakerService.callSageMakerApi(imageAvatarRequest);

        Member member = findMemberByEmail(email);
        saveImageAvatarDetails(avatarPromptRequest, predictionId, member);

        return predictionId;
    }

    private String getTranslatedPrompt(String prompt) {
        return LanguageDiscriminationUtils.translateIfKorean(prompt, translate);
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
                .version("ddfc2b08d209f9fa8c1eca692712918bd449f695dabb4a958da31802a9570fe4")
                .input(input)
                .webhook("https://api.createver.site/api/v1/image/avatar/webhook")
                .webhookEventsFilter(List.of("completed"))
                .build();
    }

    private Member findMemberByEmail(String email) {
        return email != null ? memberRepository.findByEmail(email).orElse(null) : null;
    }

    private void saveImageAvatarDetails(AvatarPromptRequest request, String predictionId, Member member) {
        ImageAvatar imageAvatar = ImageAvatar.builder()
                .prompt(request.getPrompt())
                .numSteps(request.getNumSteps())
                .styleName(request.getStyleName())
                .inputImage(request.getInputImage())
                .numOutputs(request.getNumOutputs())
                .guidanceScale(request.getGuidanceScale())
                .negativePrompt(request.getNegativePrompt())
                .styleStrengthRatio(request.getStyleStrengthRatio())
                .predictionId(predictionId)
                .member(member)
                .status("processing")
                .build();

        imageAvatarRepository.save(imageAvatar);
    }

    private String ensureContainsImgKeyword(String prompt) {
        return prompt.toLowerCase().contains("img") ? prompt : prompt + " img";
    }
}
