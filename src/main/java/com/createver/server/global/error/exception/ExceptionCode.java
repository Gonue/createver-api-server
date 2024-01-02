package com.createver.server.global.error.exception;

import lombok.Getter;

@Getter
public enum ExceptionCode {

    DUPLICATED_EMAIL(409, "이메일 중복"),
    MEMBER_NOT_FOUND(404,"멤버를 찾을 수 없음"),
    INVALID_PASSWORD(400, "잘못된 비밀번호"),
    INTERNAL_SERVER_ERROR(500,"내부 서버 오류"),
    ARTICLE_NOT_FOUND(404,"게시물을 찾을 수 없음"),
    INVALID_PERMISSION(401, "권한 없음"),
    OPENAI_API_ERROR(502, "OpenAI API 호출 실패"),
    SAGEMAKER_API_ERROR(502, "SAGEMAKER API 호출 실패"),
    S3_UPLOAD_ERROR(502, "S3 업로드 실패"),
    S3_FILE_ERROR(502,"S3 ERR"),
    GENERAL_ERROR(500, "이미지 생성 중 오류 발생"),
    GALLERY_NOT_FOUND(404, "이미지 찾을 수 없음"),
    ALREADY_LIKED(409,"이미 좋아요"),
    IMAGE_COMMENT_NOT_FOUND(404, "해당 댓글을 찾을 수 없음"),
    UNAUTHORIZED_ACCESS(401, "권한 없음"),
    ORDER_NOT_FOUND(404, "주문을 찾을 수 없음"),
    RATE_LIMIT_EXCEEDED(429, "Rate limit exceeded"),
    ALREADY_REPORTED(409, "이미 신고한 내용"),
    ALBUM_NOT_FOUND(404, "앨범을 찾을 수 없음"),
    INVALID_RATING(400, "평점은 1점에서 5점 사이의 0.5점 단위만 허용 합니다."),
    REVIEW_NOT_FOUND(404, "리뷰를 찾을 수 없음.")
    ;

    private final int status;
    private final String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
