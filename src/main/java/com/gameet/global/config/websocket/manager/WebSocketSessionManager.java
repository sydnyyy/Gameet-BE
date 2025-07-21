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

    private final ConcurrentHashMap<String, WebSocketSession> browserTabSessions = new ConcurrentHashMap<>();
    private final DiscordNotifier discordNotifier;

    public synchronized boolean register(String browserTabToken, WebSocketSession session) {
        WebSocketSession existingSession = browserTabSessions.get(browserTabToken);
        if (existingSession != null && existingSession.isOpen()) {
            try {
                log.warn("🟠 중복 WebSocket 연결 감지. browserTabToken={}, sessionId={} -> {}",
                        browserTabToken,
                        existingSession.getId(), session.getId());

                existingSession.close(new CloseStatus(4400, "Duplicate WebSocket connection"));
                discordNotifier.send(
                        "🟠 중복 WebSocket 연결 감지",
                        "- browserTabToken=" + browserTabToken + "\n"
                                + "- Session ID=" + existingSession.getId() + " -> " + session.getId() + "\n"
                                + "- 기존 세션 " + existingSession.getId() + " 종료");
            } catch (IOException e) {
                log.error("🔴 기존 WebSocket 세션 종료 실패. browserTabToken={}, sessionId={}", browserTabToken, existingSession.getId());
                return false;
            }
        }

        browserTabSessions.put(browserTabToken, session);
        return true;
    }

    public void unregister(String browserTabToken) {
        browserTabSessions.remove(browserTabToken);
    }
}
