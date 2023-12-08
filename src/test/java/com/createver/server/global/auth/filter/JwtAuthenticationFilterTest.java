package com.createver.server.global.auth.filter;

import com.createver.server.global.auth.jwt.JwtTokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@DisplayName("Jwt 인증 필터 테스트")
class JwtAuthenticationFilterTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenizer jwtTokenizer;
    @Mock
    private Authentication authentication;
    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @DisplayName("인증 시도")
    @Test
    void attemptAuthenticationTest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent("{\"email\":\"test@example.com\",\"password\":\"password\"}".getBytes());

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        Authentication result = jwtAuthenticationFilter.attemptAuthentication(request, response);

        assertNotNull(result);
        verify(authenticationManager).authenticate(any());
    }

    @DisplayName("인증 성공")
    @Test
    void successfulAuthenticationTest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(userDetails.getUsername()).thenReturn("user@example.com");
        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(jwtTokenizer.generateAccessToken(any(), any(), any(), any())).thenReturn("accessToken");
        when(jwtTokenizer.generateRefreshToken(any(), any(), any())).thenReturn("refreshToken");

        jwtAuthenticationFilter.successfulAuthentication(request, response, null, authentication);

        assertEquals("Bearer accessToken", response.getHeader("Authorization"));
        assertEquals("refreshToken", response.getHeader("Refresh"));
    }
}