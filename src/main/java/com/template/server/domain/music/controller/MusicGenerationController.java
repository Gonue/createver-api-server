package com.template.server.domain.music.controller;

import com.template.server.domain.music.dto.request.MusicPromptRequest;
import com.template.server.domain.music.service.MusicGenerationService;
import com.template.server.global.error.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/music/create")
public class MusicGenerationController {

    private final MusicGenerationService musicGenerationService;


    @PostMapping
    public Response<String> createMusic(@RequestBody MusicPromptRequest request) throws InterruptedException {
        String musicUrl = musicGenerationService.generateAndUploadMusic(request);
        return Response.success(200, musicUrl);
    }
}
