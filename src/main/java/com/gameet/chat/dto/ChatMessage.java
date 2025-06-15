package com.gameet.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gameet.chat.enums.MessageType;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessage {

    @JsonProperty("messageType")
    private MessageType messageType;

    @JsonProperty("content")
    private String content;

    @JsonProperty("matchParticipantId")
    private Long matchParticipantId;

    @JsonProperty("sendAt")
    private LocalDateTime sendAt;
}