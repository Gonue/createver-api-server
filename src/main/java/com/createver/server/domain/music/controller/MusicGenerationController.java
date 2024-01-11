package com.createver.server.domain.music.controller;

import com.createver.server.domain.music.dto.request.MusicPromptRequest;
import com.createver.server.domain.music.service.MusicGenerationService;
import com.createver.server.global.error.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/music/create")
public class MusicGenerationController {

    private final MusicGenerationService musicGenerationService;

    @PostMapping
    public CompletableFuture<ResponseEntity<Response<String>>> createMusic(@RequestBody @Valid MusicPromptRequest request) {
        return musicGenerationService.generateAndUploadMusic(request)
            .thenApply(musicUrl -> ResponseEntity.ok(Response.success(200, musicUrl)))
            .exceptionally(e -> ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.failure(500,"음악 생성 실패: " + e.getMessage())));
    }
}
