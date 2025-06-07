package com.gameet.match.entity;

import com.gameet.common.enums.PreferredGenre;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "match_success_preferred_genre")
public class MatchSuccessPreferredGenre {

    @EmbeddedId
    private MatchSuccessPreferredGenreId id;

    @MapsId("matchParticipantId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_participant_id")
    @Setter(AccessLevel.PACKAGE)
    private MatchSuccessCondition matchSuccessCondition;

    public static MatchSuccessPreferredGenre of(MatchSuccessCondition matchSuccessCondition, PreferredGenre preferredGenre) {
        return MatchSuccessPreferredGenre.builder()
                .id(MatchSuccessPreferredGenreId.of(preferredGenre))
                .matchSuccessCondition(matchSuccessCondition)
                .build();
    }
}
