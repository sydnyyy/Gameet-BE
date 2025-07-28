package com.gameet.global.config.websocket.interceptor;

import com.gameet.global.jwt.JwtUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;
    public static final String WEBSOCKET_TOKEN_KEY = "websocket_token";
    public static final String CLIENT_ID_KEY = "client_id";
    public static final String USER_ID_KEY = "user_id";

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request,
                                   @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler,
                                   @NonNull Map<String, Object> attributes) throws Exception {

        String token = getWebSocketToken(request);
        String clientId = getClientId(request);
        Long userId = jwtUtil.getUserIdFromToken(token);

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

        if (clientId == null || clientId.isBlank()) {
            log.warn("[beforeHandshake] Missing or empty Client ID in handshake request. URI: {}", request.getURI());
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }

        attributes.put(WEBSOCKET_TOKEN_KEY, token);
        attributes.put(CLIENT_ID_KEY, clientId);
        attributes.put(USER_ID_KEY, userId);

        log.info("[beforeHandshake] Valid websocket token: {}", token);

        return true;
    }

    private String getWebSocketToken(ServerHttpRequest request) {
        return UriComponentsBuilder
                .fromUri(request.getURI())
                .build()
                .getQueryParams()
                .getFirst(WEBSOCKET_TOKEN_KEY);
    }

    private String getClientId(ServerHttpRequest request) {
        return UriComponentsBuilder
                .fromUri(request.getURI())
                .build()
                .getQueryParams()
                .getFirst(CLIENT_ID_KEY);
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request,
                               @NonNull ServerHttpResponse response,
                               @NonNull WebSocketHandler wsHandler,
                               Exception exception) {

        if (exception != null) {
            log.warn("🔴 WebSocket handshake failed. Address={}", request.getRemoteAddress(), exception);
        } else {
            log.info("🟢 WebSocket handshake succeeded. Address={}", request.getRemoteAddress());
        }
    }
}
