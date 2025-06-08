package com.gameet.chat.dto;

import com.gameet.chat.enums.MessageType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {
    private Long matchParticipantId;
    private Long matchRoomId;
    private MessageType messageType;
    private String content;
}