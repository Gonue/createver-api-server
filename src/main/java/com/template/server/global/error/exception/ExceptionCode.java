package com.template.server.global.error.exception;

import lombok.Getter;

@Getter
public enum ExceptionCode {

    DUPLICATED_EMAIL(409, "이메일 중복"),
    MEMBER_NOT_FOUND(404,"멤버를 찾을 수 없음"),
    INTERNAL_SERVER_ERROR(500,"내부 서버 오류");

    private int status;
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
