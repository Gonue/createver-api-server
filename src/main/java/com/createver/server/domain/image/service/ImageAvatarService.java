package com.createver.server.domain.image.service;

import com.createver.server.domain.image.dto.request.AvatarPromptRequest;
import com.createver.server.domain.image.dto.request.ImageAvatarRequest;
import com.createver.server.domain.image.entity.ImageAvatar;
import com.createver.server.domain.image.repository.avatar.ImageAvatarRepository;
import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.repository.MemberRepository;
import com.createver.server.global.util.aws.service.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageAvatarService {

    private final RestTemplate restTemplate;
    private final S3UploadService s3UploadService;
    private final ImageAvatarRepository imageAvatarRepository;
    private final MemberRepository memberRepository;

    @Value(("${sagemaker.api-key}"))
    private String sageMakerKey;
    @Value(("${sagemaker.end-point}"))
    private String sageMakerEndPoint;

    public String generateAvatarImage(AvatarPromptRequest avatarPromptRequest, String email) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", "Token " + sageMakerKey);

        ImageAvatarRequest.Input input = ImageAvatarRequest.Input.builder()
            .prompt(avatarPromptRequest.getPrompt())
            .numSteps(avatarPromptRequest.getNumSteps())
            .styleName(avatarPromptRequest.getStyleName())
            .inputImage(avatarPromptRequest.getInputImage())
            .numOutputs(avatarPromptRequest.getNumOutputs())
            .guidanceScale(avatarPromptRequest.getGuidanceScale())
            .negativePrompt(avatarPromptRequest.getNegativePrompt())
            .styleStrengthRatio(avatarPromptRequest.getStyleStrengthRatio())
            .build();

        ImageAvatarRequest imageAvatarRequest = ImageAvatarRequest.builder()
                .version("ddfc2b08d209f9fa8c1eca692712918bd449f695dabb4a958da31802a9570fe4")
                .input(input)
//                .webhook("https://api.createver.site/api/v1/image/avatar/webhook")
                .webhook("https://7789-175-120-150-63.ngrok-free.app/api/v1/image/avatar/webhook")
                .webhookEventsFilter(List.of("completed"))
                .build();

        HttpEntity<ImageAvatarRequest> entity = new HttpEntity<>(imageAvatarRequest, httpHeaders);
        ResponseEntity<Map> response = restTemplate.postForEntity(sageMakerEndPoint, entity, Map.class);

        if (response.getBody() == null || !response.getBody().containsKey("id")) {
            throw new RuntimeException("Failed to get response from Replicate API");
        }

        String predictionId = (String) response.getBody().get("id");

        Member member = null;
        if (email != null) {
            member = memberRepository.findByEmail(email).orElse(null);
        }
        saveImageAvatarDetails(avatarPromptRequest, predictionId, member);

        return predictionId;
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
}
