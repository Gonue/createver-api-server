package com.template.server.domain.image.controller;

import com.template.server.domain.image.dto.response.GalleryResponse;
import com.template.server.domain.image.service.GalleryService;
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
public class GalleryController {

    private final GalleryService galleryService;


    @GetMapping("/list")
    public Response<Page<GalleryResponse>> galleryList(Pageable pageable){
        return Response.success(galleryService.galleryList(pageable).map(GalleryResponse::from));
    }
}
