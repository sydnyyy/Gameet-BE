package com.gameet.global.config.websocket.manager;

import com.gameet.common.service.DiscordNotifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketSessionManager {

    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final DiscordNotifier discordNotifier;

    public synchronized boolean register(String userId, WebSocketSession session) {
        WebSocketSession existingSession = sessions.get(userId);
        if (existingSession != null && existingSession.isOpen()) {
            try {
                log.warn("🟠 중복 WebSocket 연결 감지. userId: {}, sessionId: {} -> {}",
                        userId,
                        existingSession.getId(), session.getId());

                existingSession.close(CloseStatus.NORMAL);
                discordNotifier.send(
                        "🟠 중복 WebSocket 연결 감지",
                        "- User ID: " + userId + "\n"
                                + "- Session ID: " + existingSession.getId() + " -> " + session.getId() + "\n"
                                + "- 기존 세션 " + existingSession.getId() + " 종료");
            } catch (IOException e) {
                log.error("🔴 기존 WebSocket 세션 종료 실패. userId: {}, sessionId: {}", userId, existingSession.getId());
                return false;
            }
        }
        sessions.put(userId, session);
        return true;
    }

    public void unregister(String userId) {
        sessions.remove(userId);
    }
}
