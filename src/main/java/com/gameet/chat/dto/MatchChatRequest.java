package com.gameet.chat.dto;

import com.gameet.chat.enums.MessageType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchChatRequest {
    private Long matchParticipantId;
    private MessageType messageType;
    private String content;
}
