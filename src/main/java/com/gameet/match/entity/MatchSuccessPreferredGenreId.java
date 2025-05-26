package com.gameet.match.entity;

import com.gameet.common.enums.PreferredGenre;
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
public class MatchSuccessPreferredGenreId {

    @Column(name = "match_participant_id")
    private Long matchParticipantId;

    @Column(name = "preferred_genre")
    @Enumerated(EnumType.STRING)
    private PreferredGenre preferredGenre;
}
