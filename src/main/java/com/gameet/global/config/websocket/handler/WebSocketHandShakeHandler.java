package com.gameet.global.config.websocket.handler;

import com.gameet.global.config.websocket.interceptor.WebSocketAuthHandshakeInterceptor;
import com.gameet.global.jwt.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketHandShakeHandler extends DefaultHandshakeHandler {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Override
    protected Principal determineUser(@NotNull ServerHttpRequest request,
                                      @NotNull WebSocketHandler wsHandler,
                                      @NotNull Map<String, Object> attributes) {

        String verifiedToken = (String) attributes.get(WebSocketAuthHandshakeInterceptor.WEBSOCKET_TOKEN_KEY);

        if (verifiedToken == null || verifiedToken.isBlank()) {
            log.warn("[determineUser] Missing or blank verified token in handshake attributes");
            return null;
        }

        Authentication authentication = jwtAuthenticationProvider.getAuthenticationAllowExpired(verifiedToken);
        log.info("[determineUser] Authenticated user: {}", authentication.getName());

        return authentication;
    }
}
