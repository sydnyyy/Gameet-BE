package com.gameet.global.config.websocket.interceptor;

import com.gameet.global.config.websocket.manager.WebSocketSessionCoordinator;
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

    public static final String WEBSOCKET_TOKEN_KEY = "websocket_token";
    public static final String CLIENT_ID_KEY = "client_id";
    public static final String USER_ID_KEY = "user_id";

    private final JwtUtil jwtUtil;
    private final WebSocketSessionCoordinator webSocketSessionCoordinator;

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request,
                                   @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler,
                                   @NonNull Map<String, Object> attributes) throws Exception {

        String tabWebSocketToken = getWebSocketToken(request);
        String clientId = getClientId(request);
        Long userId = jwtUtil.getUserIdFromToken(tabWebSocketToken);

        if (tabWebSocketToken == null || tabWebSocketToken.isBlank()) {
            log.warn("[beforeHandshake] Missing Tab WebSocket token in handshake request");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        if (!jwtUtil.validateToken(tabWebSocketToken)) {
            log.warn("[beforeHandshake] Invalid Tab WebSocket token");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        if (webSocketSessionCoordinator.hasSession(tabWebSocketToken)) {
            log.warn("🟠 중복 WebSocket 연결 시도 감지 (새로운 세션 연결 요청 중단). tabWebSocketToken={}", tabWebSocketToken);
            response.setStatusCode(HttpStatus.CONFLICT);
            return false;
        }

        if (clientId == null || clientId.isBlank()) {
            log.warn("[beforeHandshake] Missing or empty Client ID in handshake request. URI: {}", request.getURI());
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }

        attributes.put(WEBSOCKET_TOKEN_KEY, tabWebSocketToken);
        attributes.put(CLIENT_ID_KEY, clientId);
        attributes.put(USER_ID_KEY, userId);

        log.info("[beforeHandshake] Valid tabWebSocketToken={}", tabWebSocketToken);

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
