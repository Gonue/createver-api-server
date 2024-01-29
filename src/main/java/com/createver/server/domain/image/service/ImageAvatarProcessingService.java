package com.createver.server.domain.image.service;

import com.createver.server.domain.image.dto.response.ImageAvatarWebhookResponse;
import com.createver.server.domain.image.entity.ImageAvatar;
import com.createver.server.domain.image.repository.avatar.ImageAvatarRepository;
import com.createver.server.global.util.aws.service.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageAvatarProcessingService {

    private final ImageAvatarRepository imageAvatarRepository;
    private final S3UploadService s3UploadService;

    public void processWebhookResponse(ImageAvatarWebhookResponse imageAvatarWebhookResponse) {
        String predictionId = imageAvatarWebhookResponse.getId();
        List<String> imageUrls = imageAvatarWebhookResponse.getImageUrls();

        ImageAvatar imageAvatar = imageAvatarRepository.findByPredictionId(predictionId);
        if (imageAvatar != null && imageUrls != null && !imageUrls.isEmpty()) {
            String imageUrl = imageUrls.get(0);
            String s3Url = s3UploadService.uploadFromUrl(imageUrl, "image/png");
            imageAvatar.updateResultImageAndStatus(s3Url, "completed");
            imageAvatarRepository.save(imageAvatar);
        }
    }
}
