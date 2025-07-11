package com.gameet.global.config.websocket.handler;

import com.gameet.common.service.DiscordNotifier;
import com.gameet.global.config.websocket.interceptor.WebSocketAuthHandshakeInterceptor;
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

    public CustomStompErrorHandler(WebSocketHandler delegate,
                                   DiscordNotifier discordNotifier) {
        super(delegate);
        this.discordNotifier = discordNotifier;
    }

    @Override
    public void handleTransportError(@NotNull WebSocketSession session,
                                     @NotNull Throwable exception) throws Exception {
        if (isClosedChannelException(exception)) {
            log.warn("🔴 비정상적인 채널 닫힘 감지(ClosedChannelException). Session ID: {}", session.getId());
        } else {
            log.error("🔴 WebSocket 전송 오류 발생. Session ID: {}", session.getId(), exception);
        }
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, CloseStatus closeStatus) throws Exception {
        if (closeStatus.getCode() != CloseStatus.NORMAL.getCode()) {
            log.warn("🔴 비정상적인 WebSocket 연결 종료. Session ID: {}, 상태: {}", session.getId(), closeStatus);
            discordNotifier.send(
                    "🔴 WebSocket 세션 비정상 종료 감지",
                    "- Session ID: " + session.getId() + "\n" + "- User ID: " + session.getAttributes().get(WebSocketAuthHandshakeInterceptor.USER_ID_KEY));
        } else {
            log.info("🟢 WebSocket 연결 정상 종료. Session ID: {}", session.getId());
        }
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
