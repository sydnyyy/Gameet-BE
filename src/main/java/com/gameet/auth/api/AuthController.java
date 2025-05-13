package com.gameet.auth.api;

import com.gameet.auth.dto.LoginRequest;
import com.gameet.auth.dto.SignUpRequest;
import com.gameet.auth.enums.Role;
import com.gameet.auth.service.AuthService;
import com.gameet.user.dto.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

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

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse httpServletResponse) {
        UserResponse response = authService.login(loginRequest, httpServletResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        authService.logout(httpServletRequest, httpServletResponse);
        return ResponseEntity.ok("로그아웃 성공");
    }
}
