package com.createver.server.domain.review.controller;

import com.createver.server.domain.review.dto.request.ReviewCreateRequest;
import com.createver.server.domain.review.dto.response.ReviewResponse;
import com.createver.server.domain.review.service.ReviewService;
import com.createver.server.global.error.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/review")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response<Void> createReview(@RequestBody @Valid ReviewCreateRequest request, Authentication authentication){
        String email = (authentication != null) ? authentication.getName() : null;
        reviewService.createReview(request.getRating(), request.getContent(), email);
        return Response.success(201, null);
    }

    @GetMapping("/admin")
    public Response<Page<ReviewResponse>> reviewList(Pageable pageable){
        return Response.success(reviewService.reviewList(pageable).map(ReviewResponse::from));
    }

    @DeleteMapping("/admin/{reviewId}")
    public Response<Void> deleteReview(@PathVariable Long reviewId){
        reviewService.deleteReview(reviewId);
        return Response.success();
    }

}
