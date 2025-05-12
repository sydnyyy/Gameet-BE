package com.gameet.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // User
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST,"이미 사용 중인 이메일입니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND,"존재하지 않는 유저입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    ALREADY_EXISTS_USER_PROFILE(HttpStatus.BAD_REQUEST, "유저 프로필이 이미 존재합니다."),
    USER_PROFILE_NOT_FOUND(HttpStatus.BAD_REQUEST, "유저 프로필이 존재하지 않습니다. 기본 프로필을 생성해주세요."),

    // JWT
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED,"유효하지 않은 토큰입니다."),
    USER_ID_REQUIRED(HttpStatus.BAD_REQUEST, "토큰 생성 시 userId 값은 필수입니다."),
    ROLE_REQUIRED(HttpStatus.BAD_REQUEST, "토큰 생성 시 role 값은 필수입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    JWT_PROCESSING_FAILED(HttpStatus.BAD_REQUEST, "JWT 처리 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}
