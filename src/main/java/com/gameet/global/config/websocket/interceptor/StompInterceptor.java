package com.gameet.global.config.websocket.interceptor;

import com.gameet.global.jwt.JwtAuthenticationProvider;
import com.gameet.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class StompInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    private static final Set<StompCommand> COMMANDS_TO_AUTH = Set.of(
            StompCommand.CONNECT,
            StompCommand.SUBSCRIBE,
            StompCommand.SEND
    );

    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);
        if (COMMANDS_TO_AUTH.contains(stompHeaderAccessor.getCommand())) {
            String token = jwtUtil.getAccessTokenFromAccessor(stompHeaderAccessor);

            if (token == null || token.isBlank() || !jwtUtil.validateToken(token)) {
                throw new AuthenticationCredentialsNotFoundException("인증 정보를 찾을 수 없습니다.");
            }

            Authentication authentication = jwtAuthenticationProvider.getAuthentication(token);

            boolean hasValidRole = authentication.getAuthorities().stream()
                      .anyMatch(a -> Set.of("ROLE_USER", "ROLE_GUEST").contains(a.getAuthority()));

            if (!hasValidRole ) {
                throw new AccessDeniedException("접근이 거부되었습니다.");
            }

            stompHeaderAccessor.addNativeHeader("username", authentication.getName());
            stompHeaderAccessor.setUser(authentication);
        }

        return MessageBuilder.createMessage(message.getPayload(), stompHeaderAccessor.getMessageHeaders());
    }
}
