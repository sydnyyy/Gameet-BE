package com.gameet.email.dto;

import jakarta.validation.constraints.Pattern;

public record SendEmailVerificationCodeRequest(

        @Pattern(
                regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
                message = "유효한 이메일 형식이어야 합니다. 예: example@gmail.com"
        )
        String email
) {
}
