package com.gameet.match.entity;

import com.gameet.common.entity.BaseTimeEntity;
import com.gameet.match.dto.insert.MatchParticipantInsert;
import com.gameet.user.entity.UserProfile;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "match_participant")
public class MatchParticipant extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_participant_id")
    private Long matchParticipantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_room_id", nullable = false)
    private MatchRoom matchRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

    @OneToOne(mappedBy = "matchParticipant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.PACKAGE)
    private MatchSuccessCondition matchSuccessCondition;

    @OneToOne(mappedBy = "matchParticipant", cascade = CascadeType.ALL, orphanRemoval = true)
    private MatchMannerEvaluationLog matchMannerEvaluationLog;

    @OneToMany(mappedBy = "matchParticipant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchReportLog> matchReportLog;

    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;

    public void setLastReadAt(LocalDateTime readAt) {
        this.lastReadAt = readAt;
    }

    public void setLastReadAtNow() {
        this.lastReadAt = LocalDateTime.now();
    }

    public static MatchParticipant of(MatchRoom matchRoom, MatchParticipantInsert insert) {
        MatchParticipant matchParticipant = MatchParticipant.builder()
                .matchRoom(matchRoom)
                .userProfile(insert.userProfile())
                .build();

        MatchSuccessCondition matchSuccessCondition = MatchSuccessCondition.of(matchParticipant, insert.condition());
        matchParticipant.setMatchSuccessCondition(matchSuccessCondition);

        return matchParticipant;
    }
}
