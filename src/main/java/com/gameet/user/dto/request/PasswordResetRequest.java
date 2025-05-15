package com.gameet.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PasswordResetRequest (

        @Pattern(
                regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
                message = "유효한 이메일 형식이어야 합니다. 예: example@gmail.com"
        )
        String email,

        @NotBlank(message = "비밀번호 재설정 토큰은 필수입니다.")
        String passwordResetToken,

        @NotBlank(message = "새로운 비밀번호는 필수입니다.")
        String newPassword
) {
}
