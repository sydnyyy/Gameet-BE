package com.gameet.global.config.websocket;

import com.gameet.global.config.websocket.interceptor.StompInterceptor;
import com.gameet.global.config.websocket.handler.WebSocketHandShakeHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final String ENDPOINT = "/ws";
    private static final String[] ALLOWED_ORIGINS = {"http://localhost:8000"};
    private static final String[] SIMPLE_BROKER = {"/topic", "/queue"};
    private static final String PUBLISH = "/app";
    private static final String USER_DESTINATION_PREFIX = "/user";

    private final StompInterceptor stompInterceptor;
    private final WebSocketHandShakeHandler webSocketHandShakeHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(ENDPOINT)
                .setAllowedOriginPatterns("*")
                .setHandshakeHandler(webSocketHandShakeHandler)
                .withSockJS();
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
}
