package com.createver.server.domain.review.service;

import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.repository.MemberRepository;
import com.createver.server.domain.review.dto.ReviewDto;
import com.createver.server.domain.review.entity.Review;
import com.createver.server.domain.review.repository.ReviewRepository;
import com.createver.server.global.error.exception.BusinessLogicException;
import com.createver.server.global.error.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ReviewService {

    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public void createReview(Double rating, String content, String email) {
        Member member = null;
        if (email != null && !email.trim().isEmpty()) {
            member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND, String.format("%s 을 찾을수 없음", email)));
        }

        if (rating < 0.0 || rating > 5.0 || (rating * 2) % 1 != 0) {
            throw new BusinessLogicException(ExceptionCode.INVALID_RATING);
        }
        Review review = Review.builder()
                .rating(rating)
                .content(content)
                .member(member)
                .build();

        reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public Page<ReviewDto> reviewList(Pageable pageable) {
        return reviewRepository.findAll(pageable).map(ReviewDto::from);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.REVIEW_NOT_FOUND, String.format("%s 을 찾을 수 없음", reviewId)));
        reviewRepository.delete(review);
    }
}
