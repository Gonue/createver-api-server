package com.createver.server.domain.image.controller;

import com.createver.server.domain.image.dto.request.PromptRequest;
import com.createver.server.domain.image.dto.response.CustomGenerationResponse;
import com.createver.server.domain.image.service.gallery.ImageGenerationService;
import com.createver.server.global.error.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/image/create")
public class ImageGenerationController {

    private final ImageGenerationService imageGenerationService;

    @PostMapping
    public Response<List<CustomGenerationResponse>> inputRequest(@RequestBody @Valid PromptRequest request, Authentication authentication){
        String email = null;
        if (authentication != null){
            email = authentication.getName();
        }
        List<CustomGenerationResponse> customGenerationResponses = imageGenerationService.makeImages(request, email);
        return Response.success(200, customGenerationResponses);
    }

    @PostMapping("/simple")
    public Response<List<String>> simpleImageGeneration(@RequestBody @Valid PromptRequest request) {
        List<String> s3Urls = imageGenerationService.simpleImageMake(request.getPrompt());
        return Response.success(200, s3Urls);
    }
}

