package com.createver.server.domain.music.controller;

import com.createver.server.domain.music.dto.request.MusicPromptRequest;
import com.createver.server.domain.music.dto.response.MusicWebhookResponse;
import com.createver.server.domain.music.service.MusicGenerationService;
import com.createver.server.domain.music.service.MusicProcessingService;
import com.createver.server.domain.music.service.MusicService;
import com.createver.server.global.error.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/music/create")
public class MusicGenerationController {

    private final MusicService musicService;
    private final MusicGenerationService musicGenerationService;
    private final MusicProcessingService musicProcessingService;

    @PostMapping
    public Response<String> createMusic(@RequestBody @Valid MusicPromptRequest request, Authentication authentication) {
        String predictionId = musicGenerationService.generateMusic(request, authentication.getName());
        return Response.success(200, predictionId);
    }

    @PostMapping("/webhook")
    public void handleWebhook(@RequestBody MusicWebhookResponse musicWebhookResponse) {
        musicProcessingService.processWebhookResponse(musicWebhookResponse);
    }

    @DeleteMapping("/{musicId}")
    public Response<Void> deleteMusic(@PathVariable Long musicId, Authentication authentication) {
        musicService.deleteMusic(authentication.getName(), musicId);
        return Response.success();
    }
}
