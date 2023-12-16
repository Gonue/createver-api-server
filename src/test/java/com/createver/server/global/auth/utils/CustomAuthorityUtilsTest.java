package com.createver.server.global.auth.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Custom Authority Utils 테스트")
@ExtendWith(MockitoExtension.class)
class CustomAuthorityUtilsTest {

    @InjectMocks
    private CustomAuthorityUtils customAuthorityUtils;

    @BeforeEach
    void setUp() {
        // adminMailAddresses 필드에 여러 관리자 이메일을 설정
        ReflectionTestUtils.setField(customAuthorityUtils, "adminMailAddresses", List.of("admin@example.com", "anotherAdmin@example.com"));
    }

    @Test
    @DisplayName("어드민 이메일이 주어졌을 때 관리자 권한을 생성")
    void whenAdminEmailGiven_shouldCreateAdminAuthorities() {
        // Given
        String adminEmail = "admin@example.com";

        // When
        List<GrantedAuthority> authorities = customAuthorityUtils.createAuthorities(adminEmail);

        // Then
        assertNotNull(authorities);
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("일반 사용자 이메일이 주어졌을 때 사용자 권한을 생성")
    void whenUserEmailGiven_shouldCreateUserAuthorities() {
        // Given
        String userEmail = "user@example.com";

        // When
        List<GrantedAuthority> authorities = customAuthorityUtils.createAuthorities(userEmail);

        // Then
        assertNotNull(authorities);
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        assertFalse(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("DB에 저장된 Role을 기반으로 권한 정보 생성")
    void createAuthoritiesFromRoles_shouldCreateAuthorities() {
        // Given
        List<String> roles = List.of("USER", "ADMIN");

        // When
        List<GrantedAuthority> authorities = customAuthorityUtils.createAuthorities(roles);

        // Then
        assertNotNull(authorities);
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("DB 저장 용 Role 생성")
    void createRoles_shouldCreateRoles() {
        // Given
        String adminEmail = "admin@example.com";
        String userEmail = "user@example.com";

        // When
        List<String> adminRoles = customAuthorityUtils.createRoles(adminEmail);
        List<String> userRoles = customAuthorityUtils.createRoles(userEmail);

        // Then
        assertNotNull(adminRoles);
        assertTrue(adminRoles.contains("ADMIN"));
        assertTrue(adminRoles.contains("USER"));

        assertNotNull(userRoles);
        assertTrue(userRoles.contains("USER"));
        assertFalse(userRoles.contains("ADMIN"));
    }
}


