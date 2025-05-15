package com.gameet.user.entity;

import com.gameet.global.entity.BaseTimeEntity;
import com.gameet.global.enums.GameSkillLevel;
import com.gameet.global.enums.PlayStyle;
import com.gameet.user.dto.UserProfileRequest;
import com.gameet.user.dto.UserProfileUpdateRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfile extends BaseTimeEntity {

    @Id
    @Column(name = "user_profile_id")
    private Long userProfileId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_profile_id")
    private User user;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    @Positive
    private Integer age;

    @Column(nullable = false)
    private Boolean showAge;

    @Builder.Default
    @Column(nullable = false)
    private Character gender = 'N';

    @Builder.Default
    @Column(nullable = false)
    private Integer mannerScore = 60;

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(nullable = false)
    @Setter(AccessLevel.PRIVATE)
    private List<UserPreferredGenre> preferredGenres;

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(nullable = false)
    @Setter(AccessLevel.PRIVATE)
    private List<UserGamePlatform> gamePlatforms;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlayStyle playStyle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameSkillLevel gameSkillLevel;

    @Column(nullable = false)
    private Boolean isAdultMatchAllowed;

    @Column(nullable = false)
    private Boolean isVoice;

    public static UserProfile of(User user, UserProfileRequest userProfileRequest) {
        UserProfile userProfile = UserProfile.builder()
                .user(user)
                .nickname(userProfileRequest.nickname())
                .age(userProfileRequest.age())
                .showAge(userProfileRequest.showAge())
                .gender(userProfileRequest.gender().charAt(0))
                .playStyle(userProfileRequest.playStyle())
                .gameSkillLevel(userProfileRequest.gameSkillLevel())
                .isAdultMatchAllowed(userProfileRequest.isAdultMatchAllowed())
                .isVoice(userProfileRequest.isVoice())
                .build();

        List<UserPreferredGenre> userPreferredGenres = userProfileRequest.preferredGenres().stream()
                .map(genre -> UserPreferredGenre.of(genre, userProfile))
                .toList();

        List<UserGamePlatform> userGamePlatforms = userProfileRequest.platforms().stream()
                .map(platform -> UserGamePlatform.of(platform, userProfile))
                .toList();

        userProfile.setPreferredGenres(userPreferredGenres);
        userProfile.setGamePlatforms(userGamePlatforms);

        return userProfile;
    }

    public void update(UserProfileUpdateRequest request) {
        updateIfChanged(request.nickname(), this.nickname, this::setNickname);
        updateIfChanged(request.age(), this.age, this::setAge);
        updateIfChanged(request.showAge(), this.showAge, this::setShowAge);

        if (request.gender() != null) {
            updateIfChanged(request.gender().charAt(0), this.gender, this::setGender);
        }

        updateCollection(
                this.preferredGenres,
                request.preferredGenres(),
                UserPreferredGenre::getPreferredGenre,
                genre -> UserPreferredGenre.of(genre, this)
        );

        updateCollection(
                this.gamePlatforms,
                request.platforms(),
                UserGamePlatform::getGamePlatform,
                platform -> UserGamePlatform.of(platform, this)
        );

        updateIfChanged(request.playStyle(), this.playStyle, this::setPlayStyle);
        updateIfChanged(request.gameSkillLevel(), this.gameSkillLevel, this::setGameSkillLevel);
        updateIfChanged(request.isAdultMatchAllowed(), this.isAdultMatchAllowed, this::setIsAdultMatchAllowed);
        updateIfChanged(request.isVoice(), this.isVoice, this::setIsVoice);
    }

    private <T> void updateIfChanged(T newValue, T oldValue, Consumer<T> setter) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
        }
    }

    private <E, V> void updateCollection(
            List<E> currentList,
            List<V> newValues,
            Function<E, V> valueExtractor,
            Function<V, E> entityMapper
    ) {
        if (newValues == null) return;

        Set<V> newValueSet = new HashSet<>(newValues);
        Set<V> currentValueSet = currentList.stream()
                .map(valueExtractor)
                .collect(Collectors.toSet());

        List<E> toRemove = currentList.stream()
                .filter(e -> !newValueSet.contains(valueExtractor.apply(e)))
                .toList();
        currentList.removeAll(toRemove);

        List<E> toAdd = newValues.stream()
                .filter(v -> !currentValueSet.contains(v))
                .map(entityMapper)
                .toList();
        currentList.addAll(toAdd);
    }
}
