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
    USER_NOT_FOUND_BY_EMAIL(HttpStatus.NOT_FOUND, "이메일에 해당되는 유저가 없습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    ALREADY_EXISTS_USER_PROFILE(HttpStatus.BAD_REQUEST, "유저 프로필이 이미 존재합니다."),
    USER_PROFILE_NOT_FOUND(HttpStatus.BAD_REQUEST, "유저 프로필이 존재하지 않습니다. 기본 프로필을 생성해주세요."),
    INVALID_OR_EXPIRED_PASSWORD_RESET_TOKEN(HttpStatus.UNAUTHORIZED, "비밀번호 재설정 토큰이 유효하지 않거나 만료되었습니다."),

    // JWT
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED,"유효하지 않은 토큰입니다."),
    USER_ID_REQUIRED(HttpStatus.BAD_REQUEST, "토큰 생성 시 userId 값은 필수입니다."),
    ROLE_REQUIRED(HttpStatus.BAD_REQUEST, "토큰 생성 시 role 값은 필수입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다."),
    JWT_PROCESSING_FAILED(HttpStatus.BAD_REQUEST, "JWT 처리 중 오류가 발생했습니다."),

    // email
    EMAIL_VERIFICATION_FAILED(HttpStatus.UNAUTHORIZED, "이메일 인증에 실패했습니다."),
    INVALID_EMAIL_PURPOSE(HttpStatus.BAD_REQUEST, "지원하지 않는 이메일 목적입니다."),
    EMAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송 중 문제가 발생했습니다. 잠시 후 다시 시도해주세요."),
    PASSWORD_RESET_TOKEN_REQUIRED(HttpStatus.BAD_REQUEST, "비밀번호 재설정 토큰은 필수입니다."),

    // match
    ALREADY_SEARCHING(HttpStatus.BAD_REQUEST, "이미 매칭 중입니다."),
    ALREADY_MATCHED(HttpStatus.BAD_REQUEST, "이미 매칭이 완료된 상태입니다."),
    MATCH_LOCK_ACQUISITION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "현재 매칭을 처리할 수 없습니다. 잠시 후 다시 시도해 주세요.")
    ;

    private final HttpStatus status;
    private final String message;
}
