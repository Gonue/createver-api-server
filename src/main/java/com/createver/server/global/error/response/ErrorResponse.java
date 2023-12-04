package com.createver.server.global.error.response;

import com.createver.server.global.error.exception.ExceptionCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import jakarta.validation.ConstraintViolation;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class ErrorResponse {
    private final int status;
    private final String message;
    private final Result result;

    private ErrorResponse(int status, String message, Result result) {
        this.status = status;
        this.message = message;
        this.result = result;
    }

    public static ErrorResponse of(BindingResult bindingResult) {
        Result result = new Result(FieldError.of(bindingResult), null);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid field value", result);
    }

    public static ErrorResponse of(Set<ConstraintViolation<?>> violations) {
        Result result = new Result(null, ConstraintViolationError.of(violations));
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid value", result);
    }

    public static ErrorResponse of(ExceptionCode exceptionCode) {
        return new ErrorResponse(exceptionCode.getStatus(), exceptionCode.getMessage(), null);
    }

    public static ErrorResponse of(HttpStatus httpStatus, String message) {
        return new ErrorResponse(httpStatus.value(), message, null);
    }


    @Getter
    public static class Result {
        private final List<FieldError> fieldErrors;
        private final List<ConstraintViolationError> violationErrors;

        public Result(List<FieldError> fieldErrors, List<ConstraintViolationError> violationErrors) {
            this.fieldErrors = fieldErrors;
            this.violationErrors = violationErrors;
        }
    }

    @Getter
    public static class FieldError {
        private final String field;
        private final Object rejectedValue;
        private final String reason;

        private FieldError(String field, Object rejectedValue, String reason) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.reason = reason;
        }

        public static List<FieldError> of(BindingResult bindingResult) {
            return bindingResult.getFieldErrors().stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()))
                    .collect(Collectors.toList());
        }
    }

    @Getter
    public static class ConstraintViolationError {
        private final String propertyPath;
        private final Object rejectedValue;
        private final String reason;

        private ConstraintViolationError(String propertyPath, Object rejectedValue, String reason) {
            this.propertyPath = propertyPath;
            this.rejectedValue = rejectedValue;
            this.reason = reason;
        }

        public static List<ConstraintViolationError> of(Set<ConstraintViolation<?>> constraintViolations) {
            return constraintViolations.stream()
                    .map(constraintViolation -> new ConstraintViolationError(
                            constraintViolation.getPropertyPath().toString(),
                            constraintViolation.getInvalidValue() == null ? "" : constraintViolation.getInvalidValue().toString(),
                            constraintViolation.getMessage()))
                    .collect(Collectors.toList());
        }
    }
}
