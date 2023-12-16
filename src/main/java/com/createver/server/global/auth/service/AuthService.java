package com.createver.server.global.auth.service;

import com.createver.server.global.auth.jwt.JwtTokenizer;
import com.createver.server.global.auth.utils.CustomAuthorityUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
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

    public String refreshAccessToken(String refreshToken) {
        try {
            String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
            Jws<Claims> claimsJws = jwtTokenizer.getClaims(refreshToken, base64EncodedSecretKey);
            String email = claimsJws.getBody().getSubject();

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
}
