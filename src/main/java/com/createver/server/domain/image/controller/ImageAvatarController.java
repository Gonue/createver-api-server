package com.createver.server.domain.image.controller;

import com.createver.server.domain.image.dto.request.AvatarPromptRequest;
import com.createver.server.domain.image.dto.response.ImageAvatarWebhookResponse;
import com.createver.server.domain.image.service.ImageAvatarProcessingService;
import com.createver.server.domain.image.service.ImageAvatarService;
import com.createver.server.global.error.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/image/avatar")
public class ImageAvatarController {

    private final ImageAvatarService imageAvatarService;
    private final ImageAvatarProcessingService imageAvatarProcessingService;

    @PostMapping
    public Response<String> generateAvatar(@RequestBody AvatarPromptRequest request, Authentication authentication) {
        String email = null;
        if (authentication != null) {
            email = authentication.getName();
        }
        String predictionId = imageAvatarService.generateAvatarImage(request, email);
        return Response.success(200, predictionId);
    }

    @PostMapping("/webhook")
    public void handleWebhook(@RequestBody ImageAvatarWebhookResponse imageAvatarWebhookResponse) {
        imageAvatarProcessingService.processWebhookResponse(imageAvatarWebhookResponse);
    }
}
