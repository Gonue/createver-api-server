package com.template.server.global.error.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
}
