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
class MemberAuthenticationFailureHandlerTest {

    @InjectMocks
    private MemberAuthenticationFailureHandler authenticationFailureHandler;

    @Test
    @DisplayName("인증 실패 핸들러 테스트")
    void whenAuthenticationFails_shouldHandleUnauthorized() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException authException = new AuthenticationException("Authentication Failed") {};

        // When
        authenticationFailureHandler.onAuthenticationFailure(request, response, authException);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }
}