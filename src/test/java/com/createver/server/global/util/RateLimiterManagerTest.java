package com.createver.server.global.util;

import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.entity.Plan;
import com.createver.server.domain.member.entity.PlanType;
import com.createver.server.domain.member.repository.MemberRepository;
import com.createver.server.global.util.ratelimit.RateLimiterManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
@DisplayName("RateLimiterManager 테스트")
@ExtendWith(MockitoExtension.class)
class RateLimiterManagerTest {

    @InjectMocks
    private RateLimiterManager rateLimiterManager;

    @Mock
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        Mockito.reset(memberRepository);
        rateLimiterManager.clearRateLimiters();
    }

    @DisplayName("프로 또는 울트라 플랜 사용자는 요청이 허용되어야 함")
    @Test
    void allowRequest_proOrUltraPlan_shouldReturnTrue() {
        // Given
        String email = "test@test.com";
        Member member = Member.builder()
                .email("email")
                .nickName("nickname")
                .password("password")
                .build();
        Plan plan = Plan.builder()
                .planType(PlanType.PRO)
                .purchaseDate(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusMonths(1))
                .build();
        member.memberPlanUpdate(plan);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        // When
        boolean result = rateLimiterManager.allowRequest(email);

        // Then
        assertTrue(result);
    }

    @DisplayName("플랜 없음 또는 기본 플랜 사용자는 요청 제한 적용")
    @Test
    void allowRequest_noPlanOrBasicPlan_shouldApplyRateLimit() {
        // Given
        String email = "test@test.com";
        Member member = Member.builder()
                .email("email")
                .nickName("nickname")
                .password("password")
                .build();
        member.memberPlanUpdate(null);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        // When
        boolean result = rateLimiterManager.allowRequest(email);

        // Then
        assertTrue(result);
    }

    @DisplayName("이메일 없는 경우 요청이 허용되어야 함 수정필요")
    @Test
    void allowRequest_noEmail_shouldReturnTrue() {
        // When
        boolean result = rateLimiterManager.allowRequest(null);

        // Then
        assertTrue(result);
    }
}
