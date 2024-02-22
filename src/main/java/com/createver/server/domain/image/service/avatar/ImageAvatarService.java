package com.createver.server.domain.image.service.avatar;

import com.createver.server.domain.image.dto.request.AvatarPromptRequest;
import com.createver.server.domain.image.entity.ImageAvatar;
import com.createver.server.domain.image.repository.avatar.ImageAvatarRepository;
import com.createver.server.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ImageAvatarService {

    private final ImageAvatarRepository imageAvatarRepository;

    @Transactional
    public void saveImageAvatarDetails(AvatarPromptRequest request, String predictionId, Member member) {
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
}
