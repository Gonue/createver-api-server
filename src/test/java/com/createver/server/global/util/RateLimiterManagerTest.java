package com.createver.server.global.util;

import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.entity.Plan;
import com.createver.server.domain.member.entity.PlanType;
import com.createver.server.domain.member.repository.MemberRepository;
import com.createver.server.global.util.ratelimit.RateLimiterManager;
import org.junit.jupiter.api.BeforeEach;
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

@ExtendWith(MockitoExtension.class)
public class RateLimiterManagerTest {

    @InjectMocks
    private RateLimiterManager rateLimiterManager;

    @Mock
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        Mockito.reset(memberRepository);
        rateLimiterManager.clearRateLimiters();
    }

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

    @Test
    void allowRequest_noEmail_shouldReturnTrue() {
        // When
        boolean result = rateLimiterManager.allowRequest(null);

        // Then
        assertTrue(result);
    }
}
