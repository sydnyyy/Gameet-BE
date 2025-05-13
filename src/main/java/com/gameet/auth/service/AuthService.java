package com.gameet.auth.service;

import com.gameet.auth.dto.LoginRequest;
import com.gameet.auth.dto.SignUpRequest;
import com.gameet.auth.enums.Role;
import com.gameet.auth.jwt.JwtUtil;
import com.gameet.auth.repository.TokenRepository;
import com.gameet.global.exception.CustomException;
import com.gameet.global.exception.ErrorCode;
import com.gameet.user.dto.UserResponse;
import com.gameet.user.entity.User;
import com.gameet.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;

    @Transactional
    public UserResponse registerUser(SignUpRequest signUpRequest, Role role, HttpServletResponse httpServletResponse) {
        if (userRepository.existsByEmail(signUpRequest.email())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        try {
            User user = User.of(signUpRequest, role);
            userRepository.save(user);

            issueTokenAndAttachToResponse(user.getUserId(), user.getRole(), httpServletResponse);
            return UserResponse.of(user);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    @Transactional
    public UserResponse login(LoginRequest loginRequest, HttpServletResponse httpServletResponse) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        user.verifyPasswordMatching(loginRequest.password());

        issueTokenAndAttachToResponse(user.getUserId(), user.getRole(), httpServletResponse);

        return UserResponse.of(user);
    }

    public void issueTokenAndAttachToResponse(Long userId, Role role, HttpServletResponse httpServletResponse) {
        String accessToken = jwtUtil.generateAccessToken(userId, role);
        String refreshToken = jwtUtil.generateRefreshToken(userId, role);
        httpServletResponse.setHeader(JwtUtil.HEADER_AUTHORIZATION, JwtUtil.TOKEN_PREFIX + accessToken);

        Cookie cookie = new Cookie(JwtUtil.COOKIE_REFRESH_TOKEN_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 7);
        httpServletResponse.addCookie(cookie);

        tokenRepository.saveRefreshToken(userId, refreshToken);
    }

    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String refreshToken = jwtUtil.getRefreshToken(httpServletRequest);
        if (refreshToken != null) {
            Long userId = jwtUtil.getUserIdFromToken(refreshToken);
            tokenRepository.deleteRefreshTokenByUserId(userId);
        }

        String accessToken = jwtUtil.getAccessToken(httpServletRequest);
        if (accessToken != null) {
            // TODO: BLACKLIST 추가
        }

        Cookie cookie = new Cookie(JwtUtil.COOKIE_REFRESH_TOKEN_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        httpServletResponse.addCookie(cookie);
    }
}
