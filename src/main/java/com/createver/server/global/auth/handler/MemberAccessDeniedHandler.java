package com.createver.server.global.auth.handler;

import com.createver.server.global.error.util.ErrorResponder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

// 인증에는 성공했지만 해당 리소스에 대한 권한이 없으면 호출되는 핸들러
@Slf4j
@Component
public class MemberAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Exception exception = (Exception) request.getAttribute("exception");

        String message = exception != null ? exception.getMessage() : "Access Denied";

        ErrorResponder.sendErrorResponse(response, HttpStatus.FORBIDDEN, message);

        log.warn("Forbidden error happened : {}", message);
    }
}
