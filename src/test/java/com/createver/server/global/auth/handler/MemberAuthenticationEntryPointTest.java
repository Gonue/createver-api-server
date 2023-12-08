package com.createver.server.global.auth.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("리소스 인증 실패 핸들러 테스트")
@ExtendWith(MockitoExtension.class)
class MemberAuthenticationEntryPointTest {

    @InjectMocks
    private MemberAuthenticationEntryPoint authenticationEntryPoint;

    @Test
    @DisplayName("인증 실패 핸들러 테스트")
    void whenAuthenticationFails_shouldHandleUnauthorized() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException authException = new AuthenticationException("Authentication Failed") {};

        // When
        authenticationEntryPoint.commence(request, response, authException);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    @DisplayName("인증 실패 핸들러 - 요청 속성에 예외 포함")
    void whenAuthenticationFailsWithRequestException_shouldHandleUnauthorized() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException authException = new AuthenticationException("Authentication Failed") {};
        Exception exception = new Exception("Custom Exception");
        request.setAttribute("exception", exception);

        // When
        authenticationEntryPoint.commence(request, response, authException);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }
}