package com.gameet.chat.api;

import com.gameet.chat.service.MatchChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.gameet.chat.dto.ChatMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.Principal;
import java.time.LocalDateTime;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MatchChatService matchChatService;

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessage message, Principal principal) {
        if (principal == null) {
            log.warn("WebSocket 인증 실패: principal == null");
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }

        Long matchRoomId = matchChatService.getMatchRoomIdByParticipantId(message.getMatchParticipantId());
        message.setSendAt(LocalDateTime.now());

        matchChatService.saveChat(message);

        log.info("메시지 전송 - roomId={}, participantId={}, content={}, sendAt={}",
                  matchRoomId, message.getMatchParticipantId(), message.getContent(), message.getSendAt());

        // 메시지 전송
        messagingTemplate.convertAndSend(
                  "/topic/chat.room." + matchRoomId,
                  message
        );
    }
}
