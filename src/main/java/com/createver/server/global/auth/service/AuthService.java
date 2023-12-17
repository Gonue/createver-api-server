package com.createver.server.global.auth.service;

import com.createver.server.global.auth.jwt.JwtTokenizer;
import com.createver.server.global.auth.utils.CustomAuthorityUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthorityUtils customAuthorityUtils;
    private final RedisTemplate<String, String> redisTemplate;

    public String refreshAccessToken(String refreshToken) {
        try {
            // Redis에서 리프레시 토큰 검증
            String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
            Jws<Claims> claimsJws = jwtTokenizer.getClaims(refreshToken, base64EncodedSecretKey);
            String email = claimsJws.getBody().getSubject();

            // Redis에서 리프레시 토큰 확인
            String storedRefreshToken = redisTemplate.opsForValue().get("REFRESH_TOKEN:" + email);
            if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
                throw new IllegalArgumentException("Invalid or expired refresh token");
            }

            // 새 액세스 토큰 생성
            List<String> roles = customAuthorityUtils.createRoles(email);
            Map<String, Object> claims = new HashMap<>();
            claims.put("email", email);
            claims.put("roles", roles);

            Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());
            return jwtTokenizer.generateAccessToken(claims, email, expiration, base64EncodedSecretKey);

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid refresh token", e);
        }
    }

    public void deleteRefreshToken(String email) {
        // Redis에서 리프레쉬 토큰 삭제
        redisTemplate.delete("REFRESH_TOKEN:" + email);
    }

}
