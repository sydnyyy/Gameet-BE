package com.gameet.match.entity;

import com.gameet.common.entity.BaseTimeEntity;
import com.gameet.user.entity.UserProfile;
import jakarta.persistence.*;
import lombok.*;

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
    @Setter(AccessLevel.PACKAGE)
    private MatchRoom matchRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

    @OneToOne(mappedBy = "matchParticipant", cascade = CascadeType.ALL, orphanRemoval = true)
    private MatchSuccessCondition matchSuccessCondition;

    public void setMatchSuccessCondition(MatchSuccessCondition matchSuccessCondition) {
        this.matchSuccessCondition = matchSuccessCondition;
        matchSuccessCondition.setMatchParticipant(this);
    }
}
