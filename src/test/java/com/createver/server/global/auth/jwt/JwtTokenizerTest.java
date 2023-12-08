package com.createver.server.global.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Jwt 토크나이저 테스트")
class JwtTokenizerTest {

    @DisplayName("Base64 비밀키 인코딩 테스트")
    @Test
    void testEncodeBase64SecretKey() {
        JwtTokenizer tokenizer = new JwtTokenizer();
        String secretKey = "testKey";
        String encodedKey = tokenizer.encodeBase64SecretKey(secretKey);

        assertNotNull(encodedKey);
    }

    @DisplayName("액세스 토큰 생성 테스트")
    @Test
    void testGenerateAccessToken() {
        JwtTokenizer tokenizer = new JwtTokenizer();
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String base64EncodedSecretKey = Encoders.BASE64.encode(key.getEncoded());

        Map<String, Object> claims = new HashMap<>();
        claims.put("key", "value");
        String subject = "testSubject";
        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60);

        String accessToken = tokenizer.generateAccessToken(claims, subject, expiration, base64EncodedSecretKey);

        assertNotNull(accessToken);
    }

    @DisplayName("리프레시 토큰 생성 테스트")
    @Test
    void testGenerateRefreshToken() {
        JwtTokenizer tokenizer = new JwtTokenizer();
        String subject = "testSubject";
        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60);
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String base64EncodedSecretKey = Encoders.BASE64.encode(key.getEncoded());

        String refreshToken = tokenizer.generateRefreshToken(subject, expiration, base64EncodedSecretKey);

        assertNotNull(refreshToken);
    }

    @DisplayName("클레임 추출 테스트")
    @Test
    void testGetClaims() {
        JwtTokenizer tokenizer = new JwtTokenizer();
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("key", "value");
        String subject = "testSubject";
        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60); // 1시간 후 만료
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String base64EncodedSecretKey = Encoders.BASE64.encode(key.getEncoded());

        String validToken = tokenizer.generateAccessToken(claimsMap, subject, expiration, base64EncodedSecretKey);

        // getClaims 메서드 테스트
        Jws<Claims> claims = tokenizer.getClaims(validToken, base64EncodedSecretKey);

        assertNotNull(claims);
        // 클레임의 내용을 검증할 수 있습니다.
    }

    @DisplayName("서명 검증 테스트")
    @Test
    void testVerifySignature() {
        JwtTokenizer tokenizer = new JwtTokenizer();
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String base64EncodedSecretKey = Encoders.BASE64.encode(key.getEncoded());

        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("key", "value");
        String subject = "testSubject";
        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60); // 1시간 후 만료
        String validToken = tokenizer.generateAccessToken(claimsMap, subject, expiration, base64EncodedSecretKey);

        // 서명 검증 테스트
        assertDoesNotThrow(() -> tokenizer.verifySignature(validToken, base64EncodedSecretKey));
    }

    @DisplayName("토큰 만료 시간 계산 테스트")
    @Test
    void testGetTokenExpiration() {
        JwtTokenizer tokenizer = new JwtTokenizer();
        int expirationMinutes = 60;

        Date expiration = tokenizer.getTokenExpiration(expirationMinutes);

        assertNotNull(expiration);
        // 생성된 만료 시간이 올바른지 확인
    }
}