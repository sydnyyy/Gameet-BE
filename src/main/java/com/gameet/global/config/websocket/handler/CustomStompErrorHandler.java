package com.gameet.global.config.websocket.handler;

import com.gameet.common.enums.AlertLevel;
import com.gameet.common.service.DiscordNotifier;
import com.gameet.global.config.websocket.interceptor.WebSocketAuthHandshakeInterceptor;
import com.gameet.global.config.websocket.manager.WebSocketSessionCoordinator;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.nio.channels.ClosedChannelException;

@Slf4j
public class CustomStompErrorHandler extends WebSocketHandlerDecorator {

    private final DiscordNotifier discordNotifier;
    private final WebSocketSessionCoordinator webSocketSessionCoordinator;

    public CustomStompErrorHandler(WebSocketHandler delegate,
                                   DiscordNotifier discordNotifier,
                                   WebSocketSessionCoordinator webSocketSessionCoordinator) {
        super(delegate);
        this.discordNotifier = discordNotifier;
        this.webSocketSessionCoordinator = webSocketSessionCoordinator;
    }

    @Override
    public void handleTransportError(@NotNull WebSocketSession session,
                                     @NotNull Throwable exception) throws Exception {
        String userId = session.getAttributes().get(WebSocketAuthHandshakeInterceptor.USER_ID_KEY).toString();

        if (isClosedChannelException(exception)) {
            log.warn("🔴 비정상적인 채널 닫힘 감지(ClosedChannelException). User ID: {}, Session ID: {}", userId, session.getId());
        } else {
            log.error("🔴 WebSocket 전송 오류 발생. User ID: {}, Session ID: {}", userId, session.getId(), exception);
        }
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, CloseStatus closeStatus) throws Exception {
        String tabWebSocketToken = session.getAttributes().get(WebSocketAuthHandshakeInterceptor.WEBSOCKET_TOKEN_KEY).toString();

        if (closeStatus.getCode() != CloseStatus.NORMAL.getCode()) {
            log.warn("🔴 비정상적인 WebSocket 연결 종료. tabWebSocketTokenn={}, sessionId={}, 상태: {}", tabWebSocketToken, session.getId(), closeStatus);
            String title = "🔴 WebSocket 세션 비정상 종료 감지";
            String description = "- tabWebSocketToken=" + tabWebSocketToken + "\n"
                    + "- Session ID=" + session.getId() + "\n";
            discordNotifier.send(title, description, AlertLevel.CRITICAL);
        } else {
            log.info("🟢 WebSocket 연결 정상 종료. tabWebSocketToken={}, sessionId={}", tabWebSocketToken, session.getId());
        }

        webSocketSessionCoordinator.closeSession(session);
        super.afterConnectionClosed(session, closeStatus);
    }

    private boolean isClosedChannelException(Throwable exception) {
        if (exception == null) {
            return false;
        }
        if (exception instanceof ClosedChannelException) {
            return true;
        }
        return isClosedChannelException(exception.getCause());
    }
}
