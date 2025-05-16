package com.gameet.user.entity;

import com.gameet.user.dto.request.SignUpRequest;
import com.gameet.user.enums.Role;
import com.gameet.common.entity.BaseTimeEntity;
import com.gameet.global.exception.CustomException;
import com.gameet.global.exception.ErrorCode;
import jakarta.persistence.*;
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
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.PUBLIC)
    private UserProfile userProfile;

    public static User of(SignUpRequest request, Role role) {
        return User.builder()
                .role(role)
                .email(request.email())
                .password(request.password())
                .build();
    }

    public void verifyPasswordMatching(String requestedPassword) {
        if (!password.equals(requestedPassword)) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
    }

    public void promoteToUserRole() {
        this.role = Role.USER;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}
