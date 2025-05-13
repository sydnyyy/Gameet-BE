package com.gameet.email.api;

import com.gameet.email.dto.SendEmailVerificationCodeRequest;
import com.gameet.email.dto.VerifyEmailCodeRequest;
import com.gameet.email.enums.EmailPurpose;
import com.gameet.email.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email/auth")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/sign-up/send-code")
    public ResponseEntity<?> sendSignUpCode(@RequestBody @Valid SendEmailVerificationCodeRequest sendEmailVerificationCodeRequest) {
        emailService.sendSignupCode(sendEmailVerificationCodeRequest.email(), EmailPurpose.SIGN_UP);
        return ResponseEntity.ok("회원가입 인증 코드 전송");
    }

    @PostMapping("/sign-up/verify-code")
    public ResponseEntity<?> verifySignUpCode(@RequestBody @Valid VerifyEmailCodeRequest verifyEmailCodeRequest) {
        emailService.verifyEmailCode(verifyEmailCodeRequest.email(), verifyEmailCodeRequest.code(), EmailPurpose.SIGN_UP);
        return ResponseEntity.ok("인증 성공");
    }

    @PostMapping("/password-reset/send-code")
    public ResponseEntity<?> sendPasswordResetCode(@RequestBody @Valid SendEmailVerificationCodeRequest sendEmailVerificationCodeRequest) {
        emailService.sendSignupCode(sendEmailVerificationCodeRequest.email(), EmailPurpose.PASSWORD_RESET);
        return ResponseEntity.ok("비밀번호 재설정 인증 코드 전송");
    }

    @PostMapping("/password-reset/verify-code")
    public ResponseEntity<?> verifyPasswordResetCode(@RequestBody @Valid VerifyEmailCodeRequest verifyEmailCodeRequest) {
        emailService.verifyEmailCode(verifyEmailCodeRequest.email(), verifyEmailCodeRequest.code(), EmailPurpose.PASSWORD_RESET);
        return ResponseEntity.ok("인증 성공");
    }
}
