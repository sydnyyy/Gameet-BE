package com.gameet.chat.dto;

import com.gameet.match.entity.MatchParticipant;
import lombok.Builder;

import java.util.List;

@Builder
public record MatchParticipantsInfoResponse (
    MyMatchParticipantInfoResponse myMatchParticipantInfo,
    OtherMatchParticipantInfoResponse otherMatchParticipantInfo
){
    public static MatchParticipantsInfoResponse of(List<MatchParticipant> matchParticipants, Long userProfileId) {
        MyMatchParticipantInfoResponse myMatchParticipantInfoResponse = null;
        OtherMatchParticipantInfoResponse otherMatchParticipantInfoResponse = null;

        for (MatchParticipant matchParticipant : matchParticipants) {
            if (matchParticipant.getUserProfile().getUserProfileId().equals(userProfileId)) {
                myMatchParticipantInfoResponse = MyMatchParticipantInfoResponse.of(matchParticipant);
            } else {
                otherMatchParticipantInfoResponse = OtherMatchParticipantInfoResponse.of(matchParticipant);
            }
        }

        return MatchParticipantsInfoResponse.builder()
                .myMatchParticipantInfo(myMatchParticipantInfoResponse)
                .otherMatchParticipantInfo(otherMatchParticipantInfoResponse)
                .build();
    }
}
