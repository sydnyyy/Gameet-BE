package com.gameet.global.config.websocket.listener;

import com.gameet.notification.dto.response.WebSocketPayload;
import com.gameet.notification.enums.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompSubscribeEventListener implements ApplicationListener<SessionSubscribeEvent> {

    private final SimpMessagingTemplate messagingTemplate;
    private static final String USER_NOTIFICATION_DESTINATION = "/user/queue/notify";
    private static final String TOPIC_NOTIFICATION_DESTINATION = "/topic/notify";

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();
        Principal principal = accessor.getUser();

        if (principal == null) {
            log.warn("[onApplicationEvent] No principal found. Ignoring subscribe event for destination: {}", destination);
            return;
        }

        String username = principal.getName();
        log.info("[onApplicationEvent] Subscription received. user: {}, destination: {}", username, destination);

        WebSocketPayload payload = getNotificationPayload(destination);

        if (TOPIC_NOTIFICATION_DESTINATION.equals(destination)) {
            messagingTemplate.convertAndSend("/topic/notify", payload);
        }
        else if (USER_NOTIFICATION_DESTINATION.equals(destination)) {
            messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/notify", payload);
        }
        else {
            log.warn("[onApplicationEvent] Unknown subscription destination. user: {}, destination: {}", username, destination);
        }
    }

    private WebSocketPayload getNotificationPayload(String destination) {
        if (destination == null || destination.isBlank()) {
            return WebSocketPayload.fromError(MessageType.ERROR_EMPTY_DESTINATION);
        }
        if (destination.startsWith("/user")) {
            return WebSocketPayload.fromStompSubscribe(MessageType.STOMP_SUBSCRIBE_USER, destination);
        }
        if (destination.startsWith("/topic")) {
            return WebSocketPayload.fromStompSubscribe(MessageType.STOMP_SUBSCRIBE_TOPIC, destination);
        }
        return WebSocketPayload.fromError(MessageType.ERROR_INVALID_DESTINATION);
    }
}
