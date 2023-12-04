package com.createver.server.domain.image.controller;

import com.createver.server.domain.image.service.ImageLikeService;
import com.createver.server.global.error.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/image")
public class ImageLikeController {

    private final ImageLikeService imageLikeService;

    @PostMapping("/like/{galleryId}")
    public Response<Void> addLike(@PathVariable Long galleryId, Authentication authentication){
        imageLikeService.addLike(galleryId, authentication.getName());
        return Response.success();
    }

    @GetMapping("/like/count/{galleryId}")
    public Response<Long> countLikes(@PathVariable Long galleryId){
        Long count = imageLikeService.countLikesForGallery(galleryId);
        return Response.success(200, count);
    }

    @GetMapping("/like/status/{galleryId}")
    public Response<Boolean> checkUserLikeStatus(@PathVariable Long galleryId, Authentication authentication) {
        boolean hasLiked = imageLikeService.hasUserLiked(galleryId, authentication.getName());
        return Response.success(200, hasLiked);
    }
}
