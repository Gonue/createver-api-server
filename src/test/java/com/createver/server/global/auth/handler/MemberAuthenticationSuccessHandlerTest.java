package com.createver.server.global.auth.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("리소스 인증 성공 핸들러 테스트")
@ExtendWith(MockitoExtension.class)
class MemberAuthenticationSuccessHandlerTest {

    @InjectMocks
    private MemberAuthenticationSuccessHandler authenticationSuccessHandler;

    @Test
    @DisplayName("인증 성공 핸들러 테스트")
    void whenAuthenticationSucceeds_shouldHandleSuccess() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication mockAuthentication = org.mockito.Mockito.mock(Authentication.class);

        // When
        authenticationSuccessHandler.onAuthenticationSuccess(request, response, mockAuthentication);

        // Then
        assertEquals("application/json", response.getContentType());
        assertEquals("{\"status\":200,\"message\":\"SUCCESS\",\"result\":null}", response.getContentAsString());

    }
}