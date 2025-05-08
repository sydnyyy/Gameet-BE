package com.gameet.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record SignUpRequest (

        @NotBlank(message = "유저이름은 필수입니다.")
        String username,

        @NotBlank(message = "비밀번호는 필수입니다.")
        String password,

        @NotBlank(message = "실명은 필수입니다.")
        String fullName,

        @NotNull(message = "성별은 필수입니다.")
        @Pattern(regexp = "^[FMN]$", message = "성별은 F, M, N 중 하나여야 합니다.")
        String gender,

        @NotNull(message = "나이는 필수입니다.")
        @Positive(message = "나이는 1살 이상이어야 합니다.")
        Integer age
) {
}
