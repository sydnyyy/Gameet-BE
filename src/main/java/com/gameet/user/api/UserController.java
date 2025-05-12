package com.gameet.user.api;

import com.gameet.auth.entity.UserPrincipal;
import com.gameet.user.dto.UserProfileUpdateRequest;
import com.gameet.user.service.UserService;
import com.gameet.user.dto.UserDetailsResponse;
import com.gameet.user.dto.UserProfileRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/profile")
    public ResponseEntity<?> saveUserProfile(@RequestBody @Valid UserProfileRequest userProfileRequest,
                                             @AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserDetailsResponse response = userService.saveUserProfile(userPrincipal.getUserId(), userProfileRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody UserProfileUpdateRequest userProfileUpdateRequest,
                                               @AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserDetailsResponse response = userService.updateUserProfile(userPrincipal.getUserId(), userProfileUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/profile/nickname-available")
    public ResponseEntity<Boolean> isNicknameAvailable(@RequestParam String nickname) {
        Boolean isAvailable = userService.isNicknameAvailable(nickname);
        return ResponseEntity.ok(isAvailable);
    }
}