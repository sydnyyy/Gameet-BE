package com.gameet.global.config.websocket;

import com.gameet.global.dto.StompSubscribePayload;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class StompSubscribeEventListener implements ApplicationListener<SessionSubscribeEvent> {

    private final SimpMessagingTemplate messagingTemplate;
    private static final String USER_NOTIFICATION_DESTINATION = "/user/queue/notify";

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();
        Principal principal = accessor.getUser();

        StompSubscribePayload payload = StompSubscribePayload.of(USER_NOTIFICATION_DESTINATION, "구독 성공했습니다.");

        if (USER_NOTIFICATION_DESTINATION.equals(destination) && principal != null) {
            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/notify",
                    payload
            );
        }
    }
}
