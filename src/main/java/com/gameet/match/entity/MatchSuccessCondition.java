package com.gameet.match.entity;

import com.gameet.common.entity.BaseTimeEntity;
import com.gameet.common.enums.GameSkillLevel;
import com.gameet.common.enums.PlayStyle;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
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
    @Builder.Default
    private List<MatchSuccessPreferredGenre> preferredGenres = new ArrayList<>();

    @OneToMany(mappedBy = "matchSuccessCondition", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MatchSuccessGamePlatform> gamePlatforms = new ArrayList<>();

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

    public void addMatchSuccessPreferredGenre(MatchSuccessPreferredGenre matchSuccessPreferredGenre) {
        preferredGenres.add(matchSuccessPreferredGenre);
        matchSuccessPreferredGenre.setMatchSuccessCondition(this);
    }

    public void addMatchSuccessGamePlatform(MatchSuccessGamePlatform matchSuccessGamePlatform) {
        gamePlatforms.add(matchSuccessGamePlatform);
        matchSuccessGamePlatform.setMatchSuccessCondition(this);
    }
}
