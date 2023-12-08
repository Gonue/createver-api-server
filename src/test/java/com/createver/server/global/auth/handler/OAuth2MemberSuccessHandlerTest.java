package com.createver.server.global.auth.handler;

import com.createver.server.domain.member.service.MemberService;
import com.createver.server.global.auth.jwt.JwtTokenizer;
import com.createver.server.global.auth.utils.CustomAuthorityUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("OAuth2 성공 핸들러 테스트")
class OAuth2MemberSuccessHandlerTest {

    @InjectMocks
    private OAuth2MemberSuccessHandler oAuth2MemberSuccessHandler;

    @Mock
    private JwtTokenizer jwtTokenizer;

    @Mock
    private CustomAuthorityUtils authorityUtils;

    @Mock
    private MemberService memberService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("인증 성공 테스트")
    @Test
    void testOnAuthenticationSuccess() throws Exception {
        // 설정: OAuth2 사용자 정보 및 토큰 준비
        Map<String, Object> attributes = Collections.singletonMap("email", "test@example.com");
        OAuth2User oAuth2User = new DefaultOAuth2User(Collections.emptyList(), attributes, "email");
        OAuth2AuthenticationToken token = new OAuth2AuthenticationToken(oAuth2User, Collections.emptyList(), "clientRegistrationId");
        when(authentication.getPrincipal()).thenReturn(oAuth2User);

        // 토큰 생성 관련 Mock 설정
        when(jwtTokenizer.generateAccessToken(any(), any(), any(), any())).thenReturn("accessToken");
        when(jwtTokenizer.generateRefreshToken(any(), any(), any())).thenReturn("refreshToken");

        // encodeRedirectURL 및 sendRedirect 동작 설정
        String expectedRedirectUrl = "https://createver.site/user-oauth?access_token=Bearer%20accessToken&refresh_token=refreshToken";
        when(response.encodeRedirectURL(anyString())).thenReturn(expectedRedirectUrl);

        // 메소드 실행
        oAuth2MemberSuccessHandler.onAuthenticationSuccess(request, response, token);

        // 검증
        verify(response).encodeRedirectURL(anyString());
        verify(response).sendRedirect(expectedRedirectUrl);
    }
}