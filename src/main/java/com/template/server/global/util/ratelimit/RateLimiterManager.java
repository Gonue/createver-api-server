package com.template.server.global.util.ratelimit;

import com.template.server.domain.member.entity.Member;
import com.template.server.domain.member.entity.PlanType;
import com.template.server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
public class RateLimiterManager {

    private final MemberRepository memberRepository;
    private final ConcurrentMap<String, RateLimiter> rateLimiters = new ConcurrentHashMap<>();


    public void clearRateLimiters() {
        rateLimiters.clear();
    }

    public boolean allowRequest(String email) {
        if (email != null) {
            Member member = memberRepository.findByEmail(email).orElse(null);
            if (member != null && member.getPlan() != null) {
                PlanType planType = member.getPlan().getPlanType();
                if (planType == PlanType.PRO || planType == PlanType.ULTRA) {
                    // Pro or Ultra plan members have no rate limit
                    return true;
                }
            }
            // If member is null or member's plan is null, apply rate limit
            RateLimiter rateLimiter = rateLimiters.computeIfAbsent(email, e -> new RateLimiter(1, 20_000_000_000L));
            return rateLimiter.allowRequest();
        }
        // No email provided, so no rate limit
        return true;
    }
}
