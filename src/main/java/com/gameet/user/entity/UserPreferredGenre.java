package com.gameet.user.entity;

import com.gameet.global.enums.PreferredGenre;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_profile_id", "preferred_genre"})
        }
)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserPreferredGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userPreferredGenreId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "preferred_genre")
    private PreferredGenre preferredGenre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private UserProfile userProfile;

    public static UserPreferredGenre of(PreferredGenre preferredGenre, UserProfile userProfile) {
        return UserPreferredGenre.builder()
                .preferredGenre(preferredGenre)
                .userProfile(userProfile)
                .build();
    }
}
