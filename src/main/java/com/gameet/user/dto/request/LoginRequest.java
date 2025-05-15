package com.gameet.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record LoginRequest (

        @NotBlank(message = "이메일은 필수입니다.")
        @Pattern(
                regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
                message = "유효한 이메일 형식이어야 합니다. 예: example@gmail.com"
        )
        String email,

        @NotBlank(message = "비밀번호는 필수입니다.")
        String password
) {
}
