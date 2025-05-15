package com.gameet.user.api;

import com.gameet.user.dto.request.LoginRequest;
import com.gameet.user.dto.request.SignUpRequest;
import com.gameet.user.enums.Role;
import com.gameet.user.service.AuthService;
import com.gameet.user.dto.response.EmailVerificationResponse;
import com.gameet.user.dto.request.SendEmailVerificationCodeRequest;
import com.gameet.user.dto.request.VerifyEmailCodeRequest;
import com.gameet.common.enums.EmailPurpose;
import com.gameet.user.dto.request.PasswordResetRequest;
import com.gameet.user.dto.response.UserResponse;
import com.gameet.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Operation(summary = "일반 사용자 회원가입")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공 -> GUEST 권한 부여", content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 데이터 유효성 검사 실패 or 중복 이메일", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/sign-up/user")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest, HttpServletResponse httpServletResponse) {
        UserResponse response = authService.registerUser(signUpRequest, Role.GUEST, httpServletResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/sign-up/admin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody SignUpRequest signUpRequest, HttpServletResponse httpServletResponse) {
        UserResponse response = authService.registerUser(signUpRequest, Role.ADMIN, httpServletResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "회원가입 이메일 인증 코드 전송")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 인증 코드 전송 완료", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "요청 데이터 유효성 검사 실패", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/sign-up/send-code")
    public ResponseEntity<?> sendSignUpCode(@RequestBody @Valid SendEmailVerificationCodeRequest sendEmailVerificationCodeRequest) {
        authService.sendVerificationCode(sendEmailVerificationCodeRequest.email(), EmailPurpose.SIGN_UP);
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
        authService.verifyVerificationCode(verifyEmailCodeRequest.email(), verifyEmailCodeRequest.code(), EmailPurpose.SIGN_UP);
        return ResponseEntity.ok("인증 성공");
    }

    @Operation(summary = "로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 데이터 유효성 검사 실패", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "비밀번호 불일치", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse httpServletResponse) {
        UserResponse response = authService.login(loginRequest, httpServletResponse);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그아웃")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        authService.logout(httpServletRequest, httpServletResponse);
        return ResponseEntity.ok("로그아웃 성공");
    }

    @Operation(summary = "비밀번호 재설정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 재설정 성공", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "비밀번호 재설정용 토큰 만료", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/password-reset")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid PasswordResetRequest passwordResetRequest) {
        userService.resetPassword(passwordResetRequest);
        return ResponseEntity.ok("비밀번호가 재설정되었습니다.");
    }

    @Operation(summary = "비밀번호 재설정 인증 코드 전송")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 재설정 인증 코드 전송 완료", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "요청 데이터 유효성 검사 실패", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "이메일에 해당되는 유저 없음", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/password-reset/send-code")
    public ResponseEntity<?> sendPasswordResetCode(@RequestBody @Valid SendEmailVerificationCodeRequest sendEmailVerificationCodeRequest) {
        authService.sendVerificationCode(sendEmailVerificationCodeRequest.email(), EmailPurpose.PASSWORD_RESET);
        return ResponseEntity.ok("비밀번호 재설정 코드 전송");
    }

    @Operation(summary = "비밀번호 재설성 인증 코드 검증")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 재설성 인증 코드 일치", content = @Content(schema = @Schema(implementation = EmailVerificationResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 데이터 유효성 검사 실패", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "인증 코드 불일치", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/password-reset/verify-code")
    public ResponseEntity<?> verifyPasswordResetCode(@RequestBody @Valid VerifyEmailCodeRequest verifyEmailCodeRequest) {
        EmailVerificationResponse response = authService.verifyVerificationCode(verifyEmailCodeRequest.email(), verifyEmailCodeRequest.code(), EmailPurpose.PASSWORD_RESET);
        return ResponseEntity.ok(response);
    }
}