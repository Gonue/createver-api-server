package com.template.server.global.error.advice;

import com.template.server.global.error.exception.BusinessLogicException;
import com.template.server.global.error.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {


    // BusinessLogicException 처리
    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ErrorResponse> handleBusinessLogicException(BusinessLogicException e) {
        log.error("Error occurs {}", e.toString());
        ErrorResponse response = ErrorResponse.of(HttpStatus.valueOf(e.getExceptionCode().getStatus()), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.valueOf(e.getExceptionCode().getStatus()));
    }

    // RuntimeException 처리
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        log.error("Error occurs {}", e.toString());
        ErrorResponse response = ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // MethodArgumentNotValidException 처리
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("Error occurs {}", e.toString());
        ErrorResponse response = ErrorResponse.of(e.getBindingResult());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // ConstraintViolationException 처리
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("Error occurs {}", e.toString());
        ErrorResponse response = ErrorResponse.of(e.getConstraintViolations());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // HttpRequestMethodNotSupportedException 처리
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("Error occurs {}", e.toString());
        ErrorResponse response = ErrorResponse.of(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed");
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // HttpMessageNotReadableException 처리
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("Error occurs {}", e.toString());
        ErrorResponse response = ErrorResponse.of(HttpStatus.BAD_REQUEST, "Required request body is missing");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // MissingServletRequestParameterException 처리
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("Error occurs {}", e.toString());
        ErrorResponse response = ErrorResponse.of(HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // NullPointerException 처리
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException e) {
        log.error("Null Pointer Exception: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "Null Pointer Exception occurred");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // NoHandlerFoundException 처리
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.error("Error occurs {}", e.toString());
        ErrorResponse response = ErrorResponse.of(HttpStatus.NOT_FOUND, "Invalid resource request");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 기본 예외 처리
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("# handle Exception", e);
        ErrorResponse response = ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
