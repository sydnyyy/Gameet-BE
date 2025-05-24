package com.gameet.match.entity;

import com.gameet.common.entity.BaseTimeEntity;
import com.gameet.match.enums.MatchStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
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

    @Builder.Default
    @OneToMany(mappedBy = "matchRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchParticipant> matchParticipants = new ArrayList<>();

    public void addParticipant(MatchParticipant participant) {
        matchParticipants.add(participant);
        participant.setMatchRoom(this);
    }
}
