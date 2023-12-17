package com.createver.server.global.auth.controller;

import com.createver.server.domain.image.dto.GalleryDto;
import com.createver.server.domain.image.dto.response.GalleryResponse;
import com.createver.server.global.auth.dto.RefreshTokenDto;
import com.createver.server.global.auth.service.AuthService;
import com.createver.server.global.error.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/refresh")
    public ResponseEntity<Response<String>> refreshAccessToken(@RequestBody RefreshTokenDto request) {
        String newAccessToken = authService.refreshAccessToken(request.getRefreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + newAccessToken);

        Response<String> customResponse = Response.success(200, null);
        return ResponseEntity.ok().headers(headers).body(customResponse);
    }

    @DeleteMapping("/refresh-delete")
    public Response<Void> delete(Authentication authentication){
        authService.deleteRefreshToken(authentication.getName());
        return Response.success();
    }
}