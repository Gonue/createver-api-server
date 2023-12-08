package com.createver.server.global.auth.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("리소스 접근 거부 핸들러 테스트")
@ExtendWith(MockitoExtension.class)
class MemberAccessDeniedHandlerTest {

    @InjectMocks
    private MemberAccessDeniedHandler accessDeniedHandler;

    @Test
    @DisplayName("접근 거부 핸들러 테스트")
    void handleAccessDenied() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AccessDeniedException accessDeniedException = new AccessDeniedException("Access Denied");

        // When
        accessDeniedHandler.handle(request, response, accessDeniedException);

        // Then
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    @DisplayName("접근 거부 핸들러 - 예외 포함")
    void handleAccessDeniedWithException() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AccessDeniedException accessDeniedException = new AccessDeniedException("Access Denied");

        Exception exception = new Exception("Custom Exception");
        request.setAttribute("exception", exception);

        // When
        accessDeniedHandler.handle(request, response, accessDeniedException);

        // Then
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }
}