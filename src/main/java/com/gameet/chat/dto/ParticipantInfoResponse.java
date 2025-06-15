package com.gameet.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParticipantInfoResponse {
    private Long matchParticipantId;
    private Long userProfileId;
}
