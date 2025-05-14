package com.gameet.user.api;

import com.gameet.auth.entity.UserPrincipal;
import com.gameet.user.dto.PasswordResetRequest;
import com.gameet.user.dto.UserProfileUpdateRequest;
import com.gameet.user.service.UserService;
import com.gameet.user.dto.UserDetailsResponse;
import com.gameet.user.dto.UserProfileRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping("/profile")
    @PreAuthorize("hasRole('ROLE_GUEST')")
    public ResponseEntity<?> saveUserProfile(@RequestBody @Valid UserProfileRequest userProfileRequest,
                                             @AuthenticationPrincipal UserPrincipal userPrincipal,
                                             HttpServletResponse httpServletResponse) {
        UserDetailsResponse response = userService.saveUserProfile(userPrincipal.getUserId(), userProfileRequest, httpServletResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> updateUserProfile(@RequestBody @Valid UserProfileUpdateRequest userProfileUpdateRequest,
                                               @AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserDetailsResponse response = userService.updateUserProfile(userPrincipal.getUserId(), userProfileUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/profile/nickname-available")
    public ResponseEntity<Boolean> isNicknameAvailable(@RequestParam @Size(min = 1, message = "닉네임은 1자 이상이어야 합니다.") String nickname) {
        Boolean isAvailable = userService.isNicknameAvailable(nickname);
        return ResponseEntity.ok(isAvailable);
    }

    @PostMapping("/password-reset")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid PasswordResetRequest passwordResetRequest) {
        userService.resetPassword(passwordResetRequest);
        return ResponseEntity.ok("비밀번호가 재설정되었습니다.");
    }
}