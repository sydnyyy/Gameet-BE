package com.gameet.email.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EmailPurpose {

    SIGN_UP("회원가입"),
    PASSWORD_RESET("비밀번호 재설정");

    private final String description;
}
