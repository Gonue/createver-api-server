package com.template.server.global.error.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.template.server.global.error.response.ErrorResponse;
import org.springframework.http.HttpStatus;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ErrorResponder {
    public static void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorResponse = ErrorResponse.of(status, message);
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(status.value());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
