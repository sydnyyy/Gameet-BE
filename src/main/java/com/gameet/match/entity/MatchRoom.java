package com.gameet.match.entity;

import com.gameet.common.entity.BaseTimeEntity;
import com.gameet.match.dto.request.MatchRoomInsert;
import com.gameet.match.enums.MatchStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "match_room")
public class MatchRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_room_id")
    private Long matchRoomId;

//    @Column(name = "participant_count", nullable = false)
//    private Integer participantCount;

    @Column(name = "match_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MatchStatus matchStatus;

    @OneToMany(mappedBy = "matchRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.PRIVATE)
    private List<MatchParticipant> matchParticipants;

    public static MatchRoom of(MatchRoomInsert insert) {
        MatchRoom matchRoom = MatchRoom.builder()
                .matchStatus(insert.matchStatus())
                .build();

        List<MatchParticipant> matchParticipants = insert.participants().stream()
                .map(participant -> MatchParticipant.of(matchRoom, participant))
                .toList();

        matchRoom.setMatchParticipants(matchParticipants);

        return matchRoom;
    }

    public void setMatchStatus(MatchStatus matchStatus) {
        this.matchStatus = matchStatus;
    }
}
