package com.template.server.domain.image.controller;

import com.template.server.domain.image.service.ImageLikeService;
import com.template.server.global.error.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/image")
public class ImageLikeController {

    private final ImageLikeService imageLikeService;

    @PostMapping("/like")
    public Response<Void> addLike(@RequestParam Long galleryId, Authentication authentication){
        imageLikeService.addLike(galleryId, authentication.getName());
        return Response.success();
    }

    @GetMapping("/like/count")
    public Response<Long> countLikes(@RequestParam Long galleryId){
        Long count = imageLikeService.countLikesForGallery(galleryId);
        return Response.success(200, count);
    }

    @GetMapping("/like/status")
    public Response<Boolean> checkUserLikeStatus(@RequestParam Long galleryId, Authentication authentication) {
        boolean hasLiked = imageLikeService.hasUserLiked(galleryId, authentication.getName());
        return Response.success(200, hasLiked);
    }
}
