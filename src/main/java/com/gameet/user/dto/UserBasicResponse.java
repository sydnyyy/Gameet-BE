package com.gameet.user.dto;

import com.gameet.auth.enums.Role;
import com.gameet.user.entity.User;
import lombok.Builder;

@Builder
public record UserBasicResponse (

        long userId,
        Role role,

        String username,
        String fullName,
        Character gender,
        int age
) {

    public static UserBasicResponse of(User user) {
        return UserBasicResponse.builder()
                .userId(user.getUserId())
                .role(user.getRole())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .gender(user.getGender())
                .age(user.getAge())
                .build();
    }
}
