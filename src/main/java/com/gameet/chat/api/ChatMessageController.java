package com.gameet.chat.api;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;

import com.gameet.chat.dto.ChatMessage;
import com.gameet.chat.dto.MatchChatResponse;
import com.gameet.chat.service.MatchChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController {

    private final MatchChatService matchChatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void handleChat(ChatMessage message, Principal principal) {
        try {
            Long userIdFromPrincipal = Long.parseLong(principal.getName());

            matchChatService.validateUserIdMatch(message.getMatchParticipantId(), userIdFromPrincipal);

            MatchChatResponse response = matchChatService.saveChat(message, principal);
            String destination = "/topic/chat.room." + response.getMatchRoomId();
            messagingTemplate.convertAndSend(destination, response);
        } catch (AccessDeniedException ade) {
            try {
                messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/errors", ade.getMessage());
            } catch (Exception ex) {
                log.error("Failed to send error message to user: " + principal.getName(), ex);
            }
        } catch (Exception e) {
            try {
                messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/errors", e.getMessage());
            } catch (Exception ex) {
                log.error("Failed to send error message to user: " + principal.getName(), ex);
            }
        }
    }

    @MessageExceptionHandler
    public void handleException(Throwable exception, Principal principal) {
        messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/errors", exception.getMessage());
    }
}

