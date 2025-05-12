package com.gameet.user.service;

import com.gameet.global.exception.CustomException;
import com.gameet.global.exception.ErrorCode;
import com.gameet.user.dto.UserDetailsResponse;
import com.gameet.user.dto.UserProfileRequest;
import com.gameet.user.dto.UserProfileUpdateRequest;
import com.gameet.user.entity.User;
import com.gameet.user.entity.UserProfile;
import com.gameet.user.repository.UserProfileRepository;
import com.gameet.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @Transactional
    public UserDetailsResponse saveUserProfile(Long userId, UserProfileRequest userProfileRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (user.getUserProfile() != null) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_USER_PROFILE);
        }

        UserProfile userProfile = UserProfile.of(user, userProfileRequest);
        user.setUserProfile(userProfile);

        userProfileRepository.save(userProfile);
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
}
