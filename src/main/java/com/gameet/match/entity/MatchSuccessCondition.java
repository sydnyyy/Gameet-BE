package com.gameet.match.entity;

import com.gameet.common.entity.BaseTimeEntity;
import com.gameet.common.enums.GameSkillLevel;
import com.gameet.common.enums.PlayStyle;
import com.gameet.match.dto.insert.MatchSuccessConditionInsert;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "match_success_condition")
public class MatchSuccessCondition extends BaseTimeEntity {

    @Id
    @Column(name = "match_participant_id")
    private Long matchParticipantId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "match_participant_id")
    @Setter(AccessLevel.PACKAGE)
    private MatchParticipant matchParticipant;

    @OneToMany(mappedBy = "matchSuccessCondition", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.PRIVATE)
    private List<MatchSuccessPreferredGenre> preferredGenres;

    @OneToMany(mappedBy = "matchSuccessCondition", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.PRIVATE)
    private List<MatchSuccessGamePlatform> gamePlatforms;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_skill_level")
    private GameSkillLevel gameSkillLevel;

    @Column(name = "is_adult_match_allowed", nullable = false)
    private Boolean isAdultMatchAllowed;

    @Column(name = "is_voice")
    private Boolean isVoice;

    @Column(name = "min_manner_score", nullable = false)
    private Integer minMannerScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "play_style")
    private PlayStyle playStyle;

    public static MatchSuccessCondition of(MatchParticipant matchParticipant, MatchSuccessConditionInsert insert) {
        MatchSuccessCondition matchSuccessCondition = MatchSuccessCondition.builder()
                .matchParticipant(matchParticipant)
                .gameSkillLevel(insert.gameSkillLevel())
                .isAdultMatchAllowed(insert.isAdultMatchAllowed())
                .isVoice(insert.isVoice())
                .minMannerScore(insert.minMannerScore())
                .playStyle(insert.playStyle())
                .build();

        List<MatchSuccessPreferredGenre> matchSuccessPreferredGenres = insert.preferredGenres().stream()
                .map(genre -> MatchSuccessPreferredGenre.of(matchSuccessCondition, genre))
                .toList();

        matchSuccessCondition.setPreferredGenres(matchSuccessPreferredGenres);

        List<MatchSuccessGamePlatform> matchSuccessGamePlatforms = insert.gamePlatforms().stream()
                .map(gamePlatform -> MatchSuccessGamePlatform.of(matchSuccessCondition, gamePlatform))
                .toList();

        matchSuccessCondition.setGamePlatforms(matchSuccessGamePlatforms);

        return matchSuccessCondition;
    }
}
