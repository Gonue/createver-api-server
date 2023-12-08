package com.createver.server.global.auth.userservice;

import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.repository.MemberRepository;
import com.createver.server.global.auth.utils.CustomAuthorityUtils;
import com.createver.server.global.error.exception.BusinessLogicException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@DisplayName("Member Details Service 테스트")
@ExtendWith(MockitoExtension.class)
class MemberDetailsServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CustomAuthorityUtils authorityUtils;

    @InjectMocks
    private MemberDetailsService memberDetailsService;

    @Test
    @DisplayName("유효한 사용자 이메일로 UserDetails 로드")
    void loadUserByUsername_withValidEmail_shouldReturnUserDetails() {
        // Given
        String email = "test@example.com";
        Member member = Member.builder()
                .email(email)
                .password("password")
                .roles(List.of("ROLE_USER"))
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(authorityUtils.createAuthorities(anyList())).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_USER")));

        // When
        UserDetails userDetails = memberDetailsService.loadUserByUsername(email);

        // Then
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("존재하지 않는 사용자 이메일로 UserDetails 로드 시 예외 발생")
    void loadUserByUsername_withInvalidEmail_shouldThrowException() {
        // Given
        String email = "nonexistent@example.com";
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BusinessLogicException.class, () -> memberDetailsService.loadUserByUsername(email));
    }
}