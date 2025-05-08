package com.gameet.user.entity;

import com.gameet.auth.dto.SignUpRequest;
import com.gameet.auth.enums.Role;
import com.gameet.global.entity.BaseTimeEntity;
import com.gameet.global.exception.CustomException;
import com.gameet.global.exception.ErrorCode;
import com.gameet.user.enums.PreferredTimeSlot;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, name = "full_name")
    private String fullName;

    @Column(nullable = false)
    private Character gender;

    @Column(nullable = false)
    @Positive
    private Integer age;

    @Builder.Default
    @Column(nullable = false, name = "manner_score")
    private Integer mannerScore = 60;

    private String bio;

    @Builder.Default
    @Column(name = "is_voice")
    private Boolean isVoice = Boolean.TRUE;

    @Column(name = "preferred_time_slot")
    private PreferredTimeSlot preferredTimeSlot;

    public static User of(SignUpRequest request, Role role) {
        return User.builder()
                .role(role)
                .username(request.username())
                .password(request.password())
                .fullName(request.fullName())
                .gender(request.gender().charAt(0))
                .age(request.age())
                .build();
    }

    public void verifyPasswordMatching(String requestedPassword) {
        if (!password.equals(requestedPassword)) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
    }
}
