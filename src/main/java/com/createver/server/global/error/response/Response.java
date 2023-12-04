package com.createver.server.global.error.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
public class Response<T> {

    private int status;
    private String message;
    private T result;

    public static Response<Void> success() {
        return new Response<Void>(200,"SUCCESS", null);
    }

    public static <T> Response<T> success(int status, T result) {
        return new Response<>(status,"SUCCESS", result);
    }

    public static <T> Response<Page<T>> success(Page<T> result) {
        return new Response<>(200, "SUCCESS", result);
    }

    public static <T> Response<T> failure(int status, T result) {
        return new Response<>(status, "Fail", result);
    }
}
