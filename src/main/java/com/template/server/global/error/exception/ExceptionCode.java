package com.template.server.global.error.exception;

import lombok.Getter;

@Getter
public enum ExceptionCode {

    DUPLICATED_EMAIL(409, "이메일 중복"),
    MEMBER_NOT_FOUND(404,"멤버를 찾을 수 없음"),
    INTERNAL_SERVER_ERROR(500,"내부 서버 오류"),
    ARTICLE_NOT_FOUND(404,"게시물을 찾을 수 없음"),
    INVALID_PERMISSION(401, "권한 없음"),
    OPENAI_API_ERROR(502, "OpenAI API 호출 실패"),
    S3_UPLOAD_ERROR(502, "S3 업로드 실패"),
    S3_FILE_ERROR(502,"S3 ERR"),
    GENERAL_ERROR(500, "이미지 생성 중 오류 발생"),
    GALLERY_NOT_FOUND(404, "이미지 찾을 수 없음"),
    ALREADY_LIKED(409,"이미 좋아요"),
    IMAGE_COMMENT_NOT_FOUND(404, "해당 댓글을 찾을 수 없음"),
    UNAUTHORIZED_ACCESS(401, "권한 없음")
    ;

    private int status;
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
