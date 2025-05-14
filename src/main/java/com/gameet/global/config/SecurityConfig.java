package com.gameet.global.config;

import com.gameet.auth.config.JwtAuthenticationFiler;
import com.gameet.auth.jwt.JwtAuthenticationProvider;
import com.gameet.auth.jwt.JwtUtil;
import com.gameet.global.exception.CustomAccessDeniedHandler;
import com.gameet.global.exception.CustomAuthenticationEntryPoint;
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
                        .requestMatchers(HttpMethod.POST, "/auth/sign-up/**", "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users/profile").hasRole("GUEST")
                        .requestMatchers(HttpMethod.PUT, "/users/profile").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/users/profile/nickname-available").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users/password-reset").permitAll()
                        .requestMatchers(HttpMethod.POST, "/email/auth/**").permitAll()
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

    private JwtAuthenticationFiler jwtAuthenticationFiler() {
        return new JwtAuthenticationFiler(jwtUtil, jwtAuthenticationProvider);
    }
}
