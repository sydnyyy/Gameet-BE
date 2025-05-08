package com.gameet.auth.jwt;

import com.gameet.auth.entity.UserPrincipal;
import com.gameet.auth.enums.Role;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider {

    private final JwtUtil jwtUtil;

    public Authentication getAuthentication(String token) {
        Claims claims = jwtUtil.getClaims(token);

        Long userId = claims.get("userId", Long.class);
        Role role = Role.valueOf(claims.get("role", String.class));

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .userId(userId)
                .role(role)
                .build();

        Set<SimpleGrantedAuthority> authorities = getAuthoritiesFromRole(role);

        return new UsernamePasswordAuthenticationToken(userPrincipal, token, authorities);
    }

    private Set<SimpleGrantedAuthority> getAuthoritiesFromRole(Role role) {
        if (role == Role.ADMIN) {
            return Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
