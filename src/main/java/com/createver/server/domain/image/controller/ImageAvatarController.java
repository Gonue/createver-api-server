package com.createver.server.domain.image.controller;

import com.createver.server.domain.image.dto.request.AvatarPromptRequest;
import com.createver.server.domain.image.dto.response.ImageAvatarWebhookResponse;
import com.createver.server.domain.image.service.ImageAvatarProcessingService;
import com.createver.server.domain.image.service.ImageAvatarService;
import com.createver.server.domain.image.service.ImageAvatarSseService;
import com.createver.server.global.error.response.Response;
import com.createver.server.global.util.aws.service.S3DownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/image/avatar")
public class ImageAvatarController {

    private final ImageAvatarService imageAvatarService;
    private final ImageAvatarProcessingService imageAvatarProcessingService;
    private final ImageAvatarSseService imageAvatarSseService;
    private final S3DownloadService s3DownloadService;

    @PostMapping
    public Response<String> generateAvatar(@RequestBody AvatarPromptRequest request, Authentication authentication) {
        String email = (authentication != null) ? authentication.getName() : null;
        String predictionId = imageAvatarService.generateAvatarImage(request, email);
        return Response.success(200, predictionId);
    }

    @PostMapping("/webhook")
    public void handleWebhook(@RequestBody ImageAvatarWebhookResponse imageAvatarWebhookResponse) {
        imageAvatarProcessingService.processWebhookResponse(imageAvatarWebhookResponse);
    }

    @GetMapping(value = "/stream/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@PathVariable String id) {
        SseEmitter emitter = new SseEmitter(120000L);
        imageAvatarSseService.addEmitter(id, emitter);

        emitter.onCompletion(() -> imageAvatarSseService.removeEmitter(id, emitter));
        emitter.onTimeout(() -> imageAvatarSseService.removeEmitter(id, emitter));
        emitter.onError(e -> imageAvatarSseService.removeEmitter(id, emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("connectionTest")
                    .data("Test Message - Connection Established"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        return emitter;
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
