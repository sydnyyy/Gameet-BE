package com.gameet.match.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "match_success_game_platform")
public class MatchSuccessGamePlatform {

    @EmbeddedId
    private MatchSuccessGamePlatformId id;

    @MapsId("matchParticipantId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_participant_id")
    @Setter(AccessLevel.PACKAGE)
    private MatchSuccessCondition matchSuccessCondition;
}
