package com.gameet.user.dto;

import com.gameet.auth.enums.Role;
import com.gameet.user.entity.User;
import lombok.Builder;

@Builder
public record UserResponse (

        Long userId,
        Role role,
        String email
) {

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .role(user.getRole())
                .email(user.getEmail())
                .build();
    }
}
