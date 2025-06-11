package com.gameet.chat.api;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.gameet.chat.dto.ChatMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    @MessageMapping("/chat.send")
    @SendTo("/topic/chat.room.{matchRoomId}")
    public ChatMessage sendMessage(ChatMessage message, SimpMessageHeaderAccessor accessor) {
        try {
            Principal principal = accessor.getUser();

            if (principal == null) {
                log.warn("WebSocket 인증 실패: principal이 null입니다.");
                throw new IllegalStateException("인증되지 않은 사용자입니다.");
            }

            return message;
        } catch (Exception e) {
            log.error("WebSocket 메시지 처리 중 오류 발생", e);
            throw e;
        }
    }
}

