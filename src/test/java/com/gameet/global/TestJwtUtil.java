package com.gameet.global;

import com.gameet.user.enums.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

public class TestJwtUtil {

    private static final String SECRET_KEY_TEST_ONLY = "dGVzdC1rZXktZm9yLXRlc3QtZW52LW9ubHktMTIzNDU2";
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 3;

    public static String generateTestAccessToken(Long userId, Role role) {
        return Jwts.builder()
                .setIssuer("gameet")
                .setSubject(userId.toString())
                .claim("userId", userId)
                .claim("role", role.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY_TEST_ONLY)
                .compact();
    }
}
