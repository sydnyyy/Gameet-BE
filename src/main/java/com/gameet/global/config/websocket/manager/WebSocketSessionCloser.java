package com.gameet.global.config.websocket.manager;

import com.gameet.global.config.websocket.interceptor.WebSocketAuthHandshakeInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Component
@Slf4j
public class WebSocketSessionCloser {

    public boolean tryCloseSession(WebSocketSession session) {
        String browserTabToken = session.getAttributes().get(WebSocketAuthHandshakeInterceptor.WEBSOCKET_TOKEN_KEY).toString();

        try {
            session.close(new CloseStatus(4400, "Duplicate WebSocket connection"));
        } catch (IOException e) {
            log.error("🔴 기존 WebSocket 세션 종료 실패. browserTabToken={}, sessionId={}", browserTabToken, session.getId());
            return false;
        }
        return true;
    }
}
