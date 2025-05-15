package com.gameet.user.service;

import com.gameet.auth.service.AuthService;
import com.gameet.global.exception.CustomException;
import com.gameet.global.exception.ErrorCode;
import com.gameet.user.dto.PasswordResetRequest;
import com.gameet.user.dto.UserDetailsResponse;
import com.gameet.user.dto.UserProfileRequest;
import com.gameet.user.dto.UserProfileUpdateRequest;
import com.gameet.user.entity.User;
import com.gameet.user.entity.UserProfile;
import com.gameet.user.repository.PasswordResetTokenRepository;
import com.gameet.user.repository.UserProfileRepository;
import com.gameet.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final AuthService authService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Transactional
    public UserDetailsResponse saveUserProfile(Long userId,
                                               UserProfileRequest userProfileRequest,
                                               HttpServletResponse httpServletResponse) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (user.getUserProfile() != null) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_USER_PROFILE);
        }

        UserProfile userProfile = UserProfile.of(user, userProfileRequest);
        user.setUserProfile(userProfile);

        userProfileRepository.save(userProfile);

        user.promoteToUserRole();
        authService.issueTokenAndAttachToResponse(user.getUserId(), user.getRole(), httpServletResponse);

        return UserDetailsResponse.of(user);
    }

    @Transactional
    public UserDetailsResponse updateUserProfile(Long userId, UserProfileUpdateRequest userProfileUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (user.getUserProfile() == null) {
            throw new CustomException(ErrorCode.USER_PROFILE_NOT_FOUND);
        }

        user.getUserProfile().update(userProfileUpdateRequest);
        return UserDetailsResponse.of(user);
    }

    public Boolean isNicknameAvailable(String nickname) {
        return !userProfileRepository.existsByNickname(nickname);
    }

    public Boolean isExistUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public void resetPassword(PasswordResetRequest passwordResetRequest) {
        String email = passwordResetRequest.email();
        String passwordResetToken = passwordResetRequest.passwordResetToken();
        String newPassword = passwordResetRequest.newPassword();

        Boolean isValid = passwordResetTokenRepository.isPasswordResetTokenValid(email, passwordResetToken);
        if (!isValid) {
            throw new CustomException(ErrorCode.INVALID_OR_EXPIRED_PASSWORD_RESET_TOKEN);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        user.updatePassword(newPassword);
        userRepository.save(user);

        passwordResetTokenRepository.deletePasswordResetToken(email);
    }
}
