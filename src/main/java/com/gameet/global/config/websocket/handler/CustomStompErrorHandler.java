package com.gameet.global.config.websocket.handler;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.nio.channels.ClosedChannelException;

@Slf4j
public class CustomStompErrorHandler extends WebSocketHandlerDecorator {

    public CustomStompErrorHandler(WebSocketHandler delegate) {
        super(delegate);
    }

    @Override
    public void handleTransportError(@NotNull WebSocketSession session,
                                     @NotNull Throwable exception) throws Exception {
        if (isClosedChannelException(exception)) {
            log.warn("🔴 [CustomStompErrorHandler] 비정상적인 채널 닫힘 감지(ClosedChannelException). 세션 ID: {}", session.getId());
        } else {
            log.error("🔴 [CustomStompErrorHandler] WebSocket 전송 오류 발생. 세션 ID: {}", session.getId(), exception);
        }
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        if (closeStatus.getCode() != CloseStatus.NORMAL.getCode()) {
            log.warn("🔴 [CustomStompErrorHandler] 비정상적인 WebSocket 연결 종료. 세션 ID: {}, 상태: {}", session.getId(), closeStatus);
        } else {
            log.info("🟢 [CustomStompErrorHandler] WebSocket 연결 정상 종료. 세션 ID: {}", session.getId());
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
