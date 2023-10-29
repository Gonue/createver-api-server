package com.template.server.domain.image.controller;

import com.template.server.domain.image.entity.ImageView;
import com.template.server.domain.image.service.ImageViewService;
import com.template.server.global.error.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/image")
public class ImageViewController {

    private final ImageViewService imageViewService;

    @PostMapping("/view")
    public Response<Void> addView(@RequestParam Long galleryId, Authentication authentication){
        imageViewService.addView(galleryId, authentication.getName());
        return Response.success();
    }

    @GetMapping("/view/count")
    public Response<Long> countViews(@RequestParam Long galleryId){
        Long count = imageViewService.countViewsForGallery(galleryId);
        return Response.success(200, count);
    }

    @GetMapping("/view/history")
    public Response<List<ImageView>> getViewHistory(Authentication authentication){
        List<ImageView> history = imageViewService.getViewHistory(authentication.getName());
        return Response.success(200, history);
    }
}
