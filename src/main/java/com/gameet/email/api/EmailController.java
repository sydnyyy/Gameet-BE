package com.gameet.email.api;

import com.gameet.email.dto.EmailVerificationResponse;
import com.gameet.email.dto.SendEmailVerificationCodeRequest;
import com.gameet.email.dto.VerifyEmailCodeRequest;
import com.gameet.email.enums.EmailPurpose;
import com.gameet.email.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Email", description = "이메일 인증 코드 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/email/auth")
public class EmailController {

    private final EmailService emailService;

    @Operation(summary = "회원가입 이메일 인증 코드 전송")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 인증 코드 전송 완료", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "요청 데이터 유효성 검사 실패", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/sign-up/send-code")
    public ResponseEntity<?> sendSignUpCode(@RequestBody @Valid SendEmailVerificationCodeRequest sendEmailVerificationCodeRequest) {
        emailService.sendVerificationCode(sendEmailVerificationCodeRequest.email(), EmailPurpose.SIGN_UP);
        return ResponseEntity.ok("회원가입 인증 코드 전송");
    }

    @Operation(summary = "회원가입 이메일 인증 코드 검증")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 이메일 인증 코드 일치", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "요청 데이터 유효성 검사 실패", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "인증 코드 불일치", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/sign-up/verify-code")
    public ResponseEntity<?> verifySignUpCode(@RequestBody @Valid VerifyEmailCodeRequest verifyEmailCodeRequest) {
        emailService.verifyEmailCode(verifyEmailCodeRequest.email(), verifyEmailCodeRequest.code(), EmailPurpose.SIGN_UP);
        return ResponseEntity.ok("인증 성공");
    }

    @Operation(summary = "비밀번호 재설정 인증 코드 전송")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 재설정 인증 코드 전송 완료", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "요청 데이터 유효성 검사 실패", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "이메일에 해당되는 유저 없음", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/password-reset/send-code")
    public ResponseEntity<?> sendPasswordResetCode(@RequestBody @Valid SendEmailVerificationCodeRequest sendEmailVerificationCodeRequest) {
        emailService.sendVerificationCode(sendEmailVerificationCodeRequest.email(), EmailPurpose.PASSWORD_RESET);
        return ResponseEntity.ok("비밀번호 재설정 코드 전송");
    }

    @Operation(summary = "비밀번호 재설성 인증 코드 검증")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 재설성 인증 코드 일치", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "요청 데이터 유효성 검사 실패", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "인증 코드 불일치", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/password-reset/verify-code")
    public ResponseEntity<?> verifyPasswordResetCode(@RequestBody @Valid VerifyEmailCodeRequest verifyEmailCodeRequest) {
        EmailVerificationResponse response = emailService.verifyEmailCode(verifyEmailCodeRequest.email(), verifyEmailCodeRequest.code(), EmailPurpose.PASSWORD_RESET);
        return ResponseEntity.ok(response);
    }
}
