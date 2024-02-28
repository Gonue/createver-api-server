package com.createver.server.domain.image.service.avatar;

import com.createver.server.domain.image.dto.response.ImageAvatarWebhookResponse;
import com.createver.server.domain.image.entity.ImageAvatar;
import com.createver.server.domain.image.repository.avatar.ImageAvatarRepository;
import com.createver.server.global.sse.SseService;
import com.createver.server.global.util.aws.CloudFrontUrlUtils;
import com.createver.server.global.util.aws.service.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageAvatarProcessingService {

    private final ImageAvatarRepository imageAvatarRepository;
    private final S3UploadService s3UploadService;
    private final SseService sseService;

    public void processWebhookResponse(ImageAvatarWebhookResponse imageAvatarWebhookResponse) {
        String predictionId = imageAvatarWebhookResponse.getId();
        List<String> imageUrls = imageAvatarWebhookResponse.getImageUrls();

        ImageAvatar imageAvatar = imageAvatarRepository.findByPredictionId(predictionId);
        if (imageAvatar != null && imageUrls != null && !imageUrls.isEmpty()) {
            String imageUrl = imageUrls.get(0);
            String s3Url = s3UploadService.uploadFromUrl(imageUrl, "image/png");
            String cloudFrontUrl = CloudFrontUrlUtils.convertToCloudFrontUrl(s3Url);
            imageAvatar.updateResultImageAndStatus(cloudFrontUrl, "completed");
            imageAvatarRepository.save(imageAvatar);

            sendSseEvent(predictionId, cloudFrontUrl);
        }
    }

    private void sendSseEvent(String predictionId, String imageUrl) {
        List<SseEmitter> emitters = sseService.getEmitters(predictionId);
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("imageProcessed").data(imageUrl));
                emitter.complete();
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }
    }
}
