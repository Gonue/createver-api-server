package com.createver.server.domain.review.dto.response;

import com.createver.server.domain.member.dto.response.MemberResponse;
import com.createver.server.domain.review.dto.ReviewDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewResponse {

    private final Long reviewId;
    private final Double rating;
    private final String content;
    private final LocalDateTime createdAt;
    private final MemberResponse member;

    public static ReviewResponse from(ReviewDto dto){
        MemberResponse memberResponse = null;
        if (dto.getMember() != null) {
            memberResponse = MemberResponse.from(dto.getMember());
        }
        return new ReviewResponse(
                dto.getReviewId(),
                dto.getRating(),
                dto.getContent(),
                dto.getCreatedAt(),
                memberResponse
        );
    }
}
