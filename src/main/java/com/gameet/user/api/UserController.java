package com.gameet.user.api;

import com.gameet.global.dto.UserPrincipal;
import com.gameet.user.dto.request.UserProfileRequest;
import com.gameet.user.dto.request.UserProfileUpdateRequest;
import com.gameet.user.dto.response.UserDetailsResponse;
import com.gameet.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "User Profile", description = "유저 프로필 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/profile")
@Validated
public class UserController {

    private final UserService userService;

    @Operation(summary = "프로필 최초 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "프로필 생성 성공", content = @Content(schema = @Schema(implementation = UserDetailsResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 데이터 유효성 검사 실패 or 닉네임 중복", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "ROLE=GUEST 만 접근 가능", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ROLE_GUEST')")
    public ResponseEntity<?> saveUserProfile(@RequestBody @Valid UserProfileRequest userProfileRequest,
                                             @AuthenticationPrincipal UserPrincipal userPrincipal,
                                             HttpServletResponse httpServletResponse) {
        UserDetailsResponse response = userService.saveUserProfile(userPrincipal.getUserId(), userProfileRequest, httpServletResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "프로필 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 수정 성공", content = @Content(schema = @Schema(implementation = UserDetailsResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 데이터 유효성 검사 실패", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "ROLE=USER 만 접근 가능", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PutMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> updateUserProfile(@RequestBody @Valid UserProfileUpdateRequest userProfileUpdateRequest,
                                               @AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserDetailsResponse response = userService.updateUserProfile(userPrincipal.getUserId(), userProfileUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "닉네임 유효성 검사")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "닉네임 유니크 여부 결과 반환", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "요청 데이터 유효성 검사 실패", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/nickname-available")
    public ResponseEntity<Boolean> isNicknameAvailable(@RequestParam @Size(min = 1, message = "닉네임은 1자 이상이어야 합니다.") String nickname) {
        Boolean isAvailable = userService.isNicknameAvailable(nickname);
        return ResponseEntity.ok(isAvailable);
    }
}