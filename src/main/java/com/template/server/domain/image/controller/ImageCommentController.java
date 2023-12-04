package com.template.server.domain.image.controller;


import com.template.server.domain.image.dto.ImageCommentDto;
import com.template.server.domain.image.dto.request.ImageCommentRequest;
import com.template.server.domain.image.dto.response.ImageCommentResponse;
import com.template.server.domain.image.service.ImageCommentService;
import com.template.server.global.error.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/image")
public class ImageCommentController {

    private final ImageCommentService imageCommentService;

    @PostMapping("/{galleryId}/comment")
    public Response<Void> createComment(@PathVariable Long galleryId,
                                        @RequestBody @Valid ImageCommentRequest request,
                                        Authentication authentication) {
        String email = authentication.getName();
        imageCommentService.createComment(galleryId, request.getContent(), email);
        return Response.success();
    }

    @PatchMapping("/comment/{imageCommentId}")
    public Response<ImageCommentResponse> updateComment(@PathVariable Long imageCommentId,
                                                        @RequestBody @Valid ImageCommentRequest request,
                                                        Authentication authentication) {
        String email = authentication.getName();
        ImageCommentDto updatedComment = imageCommentService.updateComment(email, imageCommentId, request.getContent());
        return Response.success(200, ImageCommentResponse.from(updatedComment));
    }

    @DeleteMapping("/comment/{imageCommentId}")
    public Response<Void> deleteComment(@PathVariable Long imageCommentId,
                                        Authentication authentication) {
        String email = authentication.getName();
        imageCommentService.deleteComment(email, imageCommentId);
        return Response.success();
    }

    @GetMapping("/{galleryId}/comments")
    public Response<Page<ImageCommentResponse>> getAllCommentsByGalleryId(@PathVariable Long galleryId,
                                                                     Pageable pageable) {
        return Response.success(imageCommentService.getAllCommentsByGalleryId(galleryId, pageable).map(ImageCommentResponse::from));
    }
}