package com.gameet.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ChatMessage {
    public enum MessageType {
        ENTER, TALK, QUIT, APPOINTMENT
    }

    @JsonProperty("messageType")
    private MessageType messageType;

    @JsonProperty("content")
    private String content;

    @JsonProperty("matchParticipantId")
    private Long matchParticipantId;
}