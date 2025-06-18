package com.gameet.global.config.websocket.handler;

import com.gameet.global.jwt.JwtAuthenticationProvider;
import com.gameet.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketHandShakeHandler extends DefaultHandshakeHandler {

    private final JwtUtil jwtUtil;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Override
    protected Principal determineUser(@NotNull ServerHttpRequest request, @NotNull WebSocketHandler wsHandler, @NotNull Map<String, Object> attributes) {
        List<String> cookieHeaders = request.getHeaders().get(HttpHeaders.COOKIE);

        String token = "";
		for (String cookieHeader : cookieHeaders) {
			String[] cookies = cookieHeader.split(";");
			for (String cookie : cookies) {
				String[] nameValue = cookie.trim().split("=", 2);
				if (nameValue.length == 2) {
					String name = nameValue[0];
					String value = nameValue[1];
					if (JwtUtil.COOKIE_WEBSOCKET_TOKEN_NAME.equals(name)) {
						token = value;
					}
				}
			}
		}

		if (token == null || token.isBlank() || !jwtUtil.validateToken(token)) {
            return null;
        }

        Authentication authentication = jwtAuthenticationProvider.getAuthentication(token);

        boolean hasRoleUser = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));

        if (!hasRoleUser) {
            return null;
        }

        return authentication;
    }
}
