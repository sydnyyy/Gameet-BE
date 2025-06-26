package com.gameet.global.config;

import com.gameet.global.exception.CustomAccessDeniedHandler;
import com.gameet.global.exception.CustomAuthenticationEntryPoint;
import com.gameet.global.jwt.JwtAuthenticationFilter;
import com.gameet.global.jwt.JwtAuthenticationProvider;
import com.gameet.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(SWAGGER_PATTERNS).permitAll()
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers("/api/match/**").hasRole("USER")
                        .requestMatchers("/api/users/auth/token/websocket").hasAnyRole("USER", "GUEST")
                        .requestMatchers(HttpMethod.POST, "/api/users/profile").hasRole("GUEST")
                        .requestMatchers(HttpMethod.PUT, "/api/users/profile").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/users/profile").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/users/profile/{userId}").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFiler(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private JwtAuthenticationFilter jwtAuthenticationFiler() {
        return new JwtAuthenticationFilter(jwtUtil, jwtAuthenticationProvider);
    }

    private static final String[] SWAGGER_PATTERNS = {
            "/swagger-ui/**",
            "/actuator/**",
            "/v3/api-docs/**",
    };

    private static final String[] PUBLIC_ENDPOINTS = {
            "/ws/**",
            "/api/users/auth/sign-up/**",
            "/api/users/auth/login",
            "/api/users/auth/password-reset/**",
            "/api/users/profile/nickname-available",
            "/api/users/auth/token/refresh",
            "/error",
    };
}
