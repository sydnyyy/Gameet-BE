package com.gameet.global.config.websocket.handler;

import com.gameet.global.config.websocket.interceptor.WebSocketAuthHandshakeInterceptor;
import com.gameet.global.config.websocket.manager.WebSocketSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

@Slf4j
public class CustomStompSessionHandler extends WebSocketHandlerDecorator {

    private final WebSocketSessionManager webSocketSessionManager;

    public CustomStompSessionHandler(WebSocketHandler delegate,
                                     WebSocketSessionManager webSocketSessionManager) {
        super(delegate);
        this.webSocketSessionManager = webSocketSessionManager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = session.getAttributes().get(WebSocketAuthHandshakeInterceptor.USER_ID_KEY).toString();
        boolean success = webSocketSessionManager.register(userId, session);
        if (!success) {
            log.warn("🟠 중복 WebSocket 연결 시도 감지(기존 세션 close 실패 & 현재 세션 강제 종료). userId: {}", userId);
            session.close(CloseStatus.PROTOCOL_ERROR);
        }
        else  {
            log.info("🟢 WebSocket 세션 연결. userId: {}, sessionId: {}", userId, session.getId());
        }
        super.afterConnectionEstablished(session);
    }
}
