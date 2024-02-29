package com.createver.server.domain.image.controller;

import com.createver.server.domain.image.dto.request.AvatarPromptRequest;
import com.createver.server.domain.image.dto.response.ImageAvatarWebhookResponse;
import com.createver.server.domain.image.service.avatar.ImageAvatarProcessingService;
import com.createver.server.domain.image.service.avatar.ImageAvatarGenerationService;
import com.createver.server.global.error.response.Response;
import com.createver.server.global.util.aws.service.S3DownloadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/image/avatar")
public class ImageAvatarController {

    private final ImageAvatarGenerationService imageAvatarService;
    private final ImageAvatarProcessingService imageAvatarProcessingService;
    private final S3DownloadService s3DownloadService;

    @PostMapping
    public Response<String> generateAvatar(@RequestBody @Valid AvatarPromptRequest request, Authentication authentication) {
        String email = (authentication != null) ? authentication.getName() : null;
        String predictionId = imageAvatarService.generateAvatarImage(request, email);
        return Response.success(200, predictionId);
    }

    @PostMapping("/webhook")
    public void handleWebhook(@RequestBody ImageAvatarWebhookResponse imageAvatarWebhookResponse) {
        imageAvatarProcessingService.processWebhookResponse(imageAvatarWebhookResponse);
    }

    @GetMapping("/download/{predictionId}")
    public ResponseEntity<byte[]> downloadImage(@PathVariable String predictionId) {
        byte[] imageData = s3DownloadService.downloadImageByPredictionId(predictionId);

        if (imageData == null) {
            return ResponseEntity.badRequest().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(imageData.length);
        headers.set("Content-Disposition", "attachment; filename=\"" + predictionId + ".png\"");
        return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
    }
}
