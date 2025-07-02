package com.gameet.global.config.websocket.interceptor;

import com.gameet.global.jwt.JwtUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;
    public static final String WEBSOCKET_TOKEN_KEY = "websocket_token";

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request,
                                   @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler,
                                   @NonNull Map<String, Object> attributes) throws Exception {

        String token = "";
        List<String> cookieHeaders = request.getHeaders().get(HttpHeaders.COOKIE);
        for (String cookieHeader : Objects.requireNonNull(cookieHeaders)) {
            String[] cookies = cookieHeader.split(";");
            for (String cookie : cookies) {
                String[] nameValue = cookie.trim().split("=", 2);
                if (nameValue.length == 2) {
                    String name = nameValue[0];
                    String value = nameValue[1];
                    if (JwtUtil.COOKIE_WEBSOCKET_TOKEN_NAME.equals(name)) {
                        token = value;
                        break;
                    }
                }
            }
        }

        if (token == null || token.isBlank()) {
            log.warn("[beforeHandshake] Missing WebSocket token in handshake request");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        if (!jwtUtil.validateToken(token)) {
            log.warn("[beforeHandshake] Invalid WebSocket token");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        attributes.put(WEBSOCKET_TOKEN_KEY, token);
        log.info("[beforeHandshake] Valid websocket token: {}", token);

        return true;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request,
                               @NonNull ServerHttpResponse response,
                               @NonNull WebSocketHandler wsHandler,
                               Exception exception) {

        if (exception != null) {
            log.warn("WebSocket handshake failed", exception);
        } else {
            log.info("WebSocket handshake succeeded from: {}", request.getRemoteAddress());
        }
    }
}
