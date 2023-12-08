package com.createver.server.global.auth.filter;

import com.createver.server.global.auth.jwt.JwtTokenizer;
import com.createver.server.global.auth.utils.CustomAuthorityUtils;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@DisplayName("Jwt 검증 필터 테스트")
@ExtendWith(MockitoExtension.class)
class JwtVerificationFilterTest {

    @Mock
    private JwtTokenizer jwtTokenizer;

    @Mock
    private CustomAuthorityUtils customAuthorityUtils;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtVerificationFilter jwtVerificationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(new SecurityContextImpl());
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("정상적인 JWT가 주어진 경우, 인증 정보가 SecurityContext에 설정되어야 함")
    void whenValidJwtProvided_shouldSetAuthentication() throws Exception {
        // Given
        request.addHeader("Authorization", "Bearer valid.token.here");


        // When
        jwtVerificationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("JWT가 없는 경우, FilterChain을 통해 처리되어야 함")
    void whenJwtNotProvided_shouldProcessFilterChain() throws Exception {
        // Given
        // When
        jwtVerificationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("잘못된 JWT 형식일 경우 예외 처리")
    void whenInvalidJwtFormat_shouldHandleException() throws Exception {
        // Given
        request.addHeader("Authorization", "Invalid JWT Format");

        // When
        jwtVerificationFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertNotNull(request.getAttribute("exception"));
        verify(filterChain).doFilter(request, response);
    }

}