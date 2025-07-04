package com.gameet.global.config.websocket;

import com.gameet.global.config.websocket.interceptor.StompInterceptor;
import com.gameet.global.config.websocket.handler.WebSocketHandShakeHandler;
import com.gameet.global.config.websocket.interceptor.WebSocketAuthHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.nio.channels.ClosedChannelException;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

    private static final String ENDPOINT = "/ws";
    private static final String[] ALLOWED_ORIGINS = {"https://gameet.vercel.app/", "http://localhost:8000"};
    private static final String[] SIMPLE_BROKER = {"/topic", "/queue"};
    private static final String PUBLISH = "/app";
    private static final String USER_DESTINATION_PREFIX = "/user";

    private final StompInterceptor stompInterceptor;
    private final WebSocketAuthHandshakeInterceptor webSocketAuthHandshakeInterceptor;
    private final WebSocketHandShakeHandler webSocketHandShakeHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(ENDPOINT)
                .setAllowedOriginPatterns(ALLOWED_ORIGINS)
                .addInterceptors(webSocketAuthHandshakeInterceptor)
                .setHandshakeHandler(webSocketHandShakeHandler)
                .withSockJS();

        registry.addEndpoint(ENDPOINT)
                .setAllowedOriginPatterns(ALLOWED_ORIGINS)
                .addInterceptors(webSocketAuthHandshakeInterceptor)
                .setHandshakeHandler(webSocketHandShakeHandler);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(SIMPLE_BROKER);
        registry.setApplicationDestinationPrefixes(PUBLISH);
        registry.setUserDestinationPrefix(USER_DESTINATION_PREFIX);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompInterceptor);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.addDecoratorFactory(CustomStompErrorHandler::new);
    }

    static class CustomStompErrorHandler extends WebSocketHandlerDecorator {

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
}
