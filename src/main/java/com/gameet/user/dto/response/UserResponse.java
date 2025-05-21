package com.gameet.user.dto.response;

import com.gameet.user.enums.Role;
import com.gameet.user.entity.User;
import lombok.Builder;

@Builder
public record UserResponse (

        Long userId,
        Role role,
        String email,
        String accessToken
) {

    public static UserResponse of(User user, String accessToken) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .role(user.getRole())
                .email(user.getEmail())
                .accessToken(accessToken)
                .build();
    }
}
