package com.gameet.user.dto.response;

import lombok.Builder;

@Builder
public record EmailVerificationResponse (

        String email,
        String passwordResetToken,
        String message
) {

    public static EmailVerificationResponse of(String email, String passwordResetToken) {
        return EmailVerificationResponse.builder()
                .email(email)
                .passwordResetToken(passwordResetToken)
                .message("비밀번호 재설정 인증에 성공했습니다.")
                .build();
    }
}
