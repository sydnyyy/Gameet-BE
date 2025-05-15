package com.gameet.user.entity;

import com.gameet.common.enums.GamePlatform;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_profile_id", "game_platform"})
        }
)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserGamePlatform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userGamePlatformId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "game_platform")
    private GamePlatform gamePlatform;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private UserProfile userProfile;

    public static UserGamePlatform of(GamePlatform gamePlatform, UserProfile userProfile) {
        return UserGamePlatform.builder()
                .gamePlatform(gamePlatform)
                .userProfile(userProfile)
                .build();
    }
}
