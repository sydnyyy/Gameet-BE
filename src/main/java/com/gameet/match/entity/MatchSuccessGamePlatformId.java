package com.gameet.match.entity;

import com.gameet.common.enums.GamePlatform;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Embeddable
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchSuccessGamePlatformId {

    @Column(name = "match_participant_id")
    private Long matchParticipantId;

    @Column(name = "game_platform")
    @Enumerated(EnumType.STRING)
    private GamePlatform gamePlatform;
}
