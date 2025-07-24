package com.gameet.global.config.websocket.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

@Component
@RequiredArgsConstructor
public class WebSocketSessionCoordinator {

    private final WebSocketSessionManager webSocketSessionManager;
    private final WebSocketSessionCloser webSocketSessionCloser;

    public boolean registerSession(WebSocketSession session) {
        return webSocketSessionManager.register(session);
    }

    public void closeSession(WebSocketSession session) {
        webSocketSessionManager.unregisterSession(session);
        webSocketSessionCloser.tryCloseSession(session, CloseStatus.NORMAL);
    }

    public void closeSessionsOnLogout(Long userId) {
        webSocketSessionManager.closeSessionsOnLogout(userId);
    }
}
