package com.gameet.global.config.websocket.handler;

import com.gameet.global.config.websocket.interceptor.WebSocketAuthHandshakeInterceptor;
import com.gameet.global.config.websocket.manager.WebSocketSessionCoordinator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

@Slf4j
public class CustomStompSessionHandler extends WebSocketHandlerDecorator {

    private final WebSocketSessionCoordinator webSocketSessionCoordinator;

    public CustomStompSessionHandler(WebSocketHandler delegate,
                                     WebSocketSessionCoordinator webSocketSessionCoordinator) {
        super(delegate);
        this.webSocketSessionCoordinator = webSocketSessionCoordinator;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String browserTabToken = session.getAttributes().get(WebSocketAuthHandshakeInterceptor.WEBSOCKET_TOKEN_KEY).toString();
        webSocketSessionCoordinator.registerSession(session);

        log.info("🟢 WebSocket 세션 등록. browserTabToken={}, sessionId={}", browserTabToken, session.getId());

        super.afterConnectionEstablished(session);
    }
}
