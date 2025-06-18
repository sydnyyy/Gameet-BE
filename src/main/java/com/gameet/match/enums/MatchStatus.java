package com.gameet.match.enums;

import com.gameet.global.exception.CustomException;
import com.gameet.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MatchStatus {

    NONE("상태 없음"),
    SEARCHING("매칭 중"),
    CANCEL("매칭 취소"),
    MATCHED("매칭 완료"),
    FAILED("매칭 실패"),
    COMPLETED("매칭 종료");

    private final String name;

    public static String getMessage(MatchStatus matchStatus) {
        return switch (matchStatus) {
            case SEARCHING -> "매칭 중입니다.";
            case CANCEL -> "매칭 취소되었습니다.";
            case MATCHED -> "매칭 완료되었습니다.";
            case FAILED -> "매칭 실패했습니다.";
            case COMPLETED -> "매칭 종료되었습니다.";
            default -> throw new CustomException(ErrorCode.INVALID_MATCH_STATUS);
        };
    }
}
