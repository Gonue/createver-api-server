package com.createver.server.domain.review.dto;

import com.createver.server.domain.member.dto.MemberDto;
import com.createver.server.domain.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {
    private Long reviewId;
    private Double rating;
    private String content;
    private MemberDto member;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static ReviewDto from(Review entity){
        MemberDto memberDto = null;
        if (entity.getMember() != null) {
            memberDto = MemberDto.from(entity.getMember());
        }
        return new ReviewDto(
                entity.getReviewId(),
                entity.getRating(),
                entity.getContent(),
                memberDto,
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }
}
