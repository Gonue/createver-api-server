package com.createver.server.domain.review.service;

import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.repository.MemberRepository;
import com.createver.server.domain.review.entity.Review;
import com.createver.server.domain.review.repository.ReviewRepository;
import com.createver.server.global.error.exception.BusinessLogicException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Review Service 테스트")
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Member member;
    private Review review;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email("user@example.com")
                .nickName("TestUser")
                .password("password")
                .profileImage("url")
                .roles(List.of("ROLE_USER"))
                .build();
        review = Review.builder()
                .rating(4.5)
                .content("Great article!")
                .member(member)
                .build();
    }

    @Test
    @DisplayName("Review 생성 테스트")
    void createReviewTest() {
        // Given
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));

        // When
        reviewService.createReview(4.5, "Great article!", "user@example.com");

        // Then
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    @DisplayName("Review 목록 조회 테스트")
    void reviewListTest() {
        // Given
        Pageable pageable = Pageable.unpaged();
        when(reviewRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(review)));

        // When
        var result = reviewService.reviewList(pageable);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(reviewRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Review 삭제 테스트")
    void deleteReviewTest() {
        // Given
        Long reviewId = 1L;
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // When
        reviewService.deleteReview(reviewId);

        // Then
        verify(reviewRepository).delete(review);
    }

    @Test
    @DisplayName("이메일이 null이거나 공백인 경우 처리 테스트")
    void createReviewWithEmailNullEmptyTest() {
        // email이 null인 경우
        reviewService.createReview(4.5, "test review", null);
        verify(reviewRepository, times(1)).save(any(Review.class));

        // email이 공백인 경우
        reviewService.createReview(4.5, "test review", "");
        verify(reviewRepository, times(2)).save(any(Review.class));
    }

    @Test
    @DisplayName("존재하지 않는 리뷰 삭제 시 예외 발생 테스트")
    void deleteNonExistentReviewTest() {
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(BusinessLogicException.class, () -> reviewService.deleteReview(999L));
    }

    @Test
    @DisplayName("유효하지 않은 평점 값에 대한 예외 처리 테스트")
    void createReviewWithInvalidRatingTest() {
        assertThrows(BusinessLogicException.class, () -> reviewService.createReview(-1.0, "Bad rating", "user@example.com"));
        assertThrows(BusinessLogicException.class, () -> reviewService.createReview(6.0, "Bad rating", "user@example.com"));
        assertThrows(BusinessLogicException.class, () -> reviewService.createReview(3.7, "Bad rating", "user@example.com"));
    }
}
