package com.template.server.global.auth.handler;

import com.template.server.global.error.util.ErrorResponder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

//인증오류가 발생할때 호출되는 핸들러
@Slf4j
@Component
public class MemberAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Exception exception = (Exception) request.getAttribute("exception");

        String message = exception != null ? exception.getMessage() : authException.getMessage();

        ErrorResponder.sendErrorResponse(response, HttpStatus.UNAUTHORIZED, message);

        log.warn("Unauthorized error happened : {}", message);
    }
}