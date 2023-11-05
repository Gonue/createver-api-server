package com.template.server.domain.image.controller;

import com.template.server.domain.image.dto.ImageTagDto;
import com.template.server.domain.image.entity.ImageTag;
import com.template.server.domain.image.service.ImageTagService;
import com.template.server.global.error.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/image")
public class ImageTagController {

    private final ImageTagService imageTagService;

    @GetMapping("/tags")
    public Response<Page<ImageTagDto>> getAllTags(Pageable pageable) {
        Page<ImageTagDto> tags = imageTagService.getAllTags(pageable);
        return Response.success(200,tags);
    }
}