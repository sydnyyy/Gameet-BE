package com.gameet.auth.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gameet.auth.dto.LoginRequest;
import com.gameet.auth.dto.SignUpRequest;
import com.gameet.auth.enums.Role;
import com.gameet.user.entity.User;
import com.gameet.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("[회원가입 성공 테스트] 회원가입 성공하면 JWT 발급")
    public void sign_up_success_test() throws Exception {
        // given
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .email("a123@gmail.com")
                .password("password")
                .build();

        // when & then
        mockMvc.perform(
                post("/auth/sign-up/user")
                        .content(objectMapper.writeValueAsString(signUpRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Authorization", notNullValue()))
                .andExpect(header().string("Authorization", startsWith("Bearer ")))
                .andExpect(cookie().exists("refresh_token"))
                .andDo(print());
    }

    @Test
    @DisplayName("[회원가입 성공 테스트] 회원가입 성공하면 User PK, 이메일 반환")
    public void sign_up_success_returns_basic_user_info() throws Exception {
        // given
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .email("b123@gmail.com")
                .password("password")
                .build();

        // when & then
        mockMvc.perform(
                post("/auth/sign-up/user")
                        .content(objectMapper.writeValueAsString(signUpRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").isNumber())
                .andExpect(jsonPath("$.email").value(signUpRequest.email()))
                .andDo(print());
    }

    @Test
    @DisplayName("[회원가입 ROLE(USER) 테스트] /sign-up/user 로 요청 시 ROLE=USER")
    public void user_sign_up_should_assign_user_role() throws Exception {
        // given
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .email("a345@gmail.com")
                .password("password")
                .build();

        // when & then
        mockMvc.perform(
                post("/auth/sign-up/user")
                        .content(objectMapper.writeValueAsString(signUpRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value(Role.USER.name()))
                .andDo(print());
    }

    @Test
    @DisplayName("[회원가입 ROLE(ADMIN) 테스트] /sign-up/admin 로 요청 시 ROLE=ADMIN")
    public void admin_sign_up_should_assign_admin_role() throws Exception{
        // given
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .email("321@gmail.com")
                .password("password")
                .build();

        // when & then
        mockMvc.perform(
                post("/auth/sign-up/admin")
                        .content(objectMapper.writeValueAsString(signUpRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value(Role.ADMIN.name()))
                .andDo(print());
    }

    @Test
    @DisplayName("[회원가입 유효성 테스트] 잘못된 이메일 형식이면 회원가입 불가")
    public void sign_up_should_fail_when_email_has_invalid_format() throws Exception {
        // given
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .email("use-143.com")
                .password("password")
                .build();

        // when & then
        mockMvc.perform(
                post("/auth/sign-up/user")
                        .content(objectMapper.writeValueAsString(signUpRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("유효한 이메일 형식이어야 합니다. 예: example@gmail.com"))
                .andDo(print());
    }

    @Test
    @DisplayName("[로그인 성공 테스트] 로그인 성공하면 JWT 발급")
    public void login_success_test() throws Exception {
        // given
        User user = User.builder()
                .role(Role.USER)
                .email("as123@gmail.com")
                .password("password")
                .build();

        userRepository.save(user);

        LoginRequest loginRequest = LoginRequest.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();

        // when & then
        mockMvc.perform(
                post("/auth/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", notNullValue()))
                .andExpect(header().string("Authorization", startsWith("Bearer ")))
                .andExpect(cookie().exists("refresh_token"))
                .andDo(print());
    }

    @Test
    @DisplayName("[로그인 유효성 테스트] 이메일 필수")
    public void login_should_fail_when_email_is_blank() throws Exception {
        // given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("")
                .password("password")
                .build();

        // when & then
        mockMvc.perform(
                post("/auth/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("유효한 이메일 형식이어야 합니다. 예: example@gmail.com"))
                .andDo(print());
    }

    @Test
    @DisplayName("[로그아웃 성공 테스트] 로그아웃 시 Access Token 제거 & Refresh Token 만료")
    public void logout_success_test () throws Exception {
        // given
        User user = User.builder()
                .role(Role.USER)
                .email("ab321@gmail.com")
                .password("password")
                .build();

        userRepository.save(user);

        LoginRequest loginRequest = LoginRequest.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();

        // when & then
        String accessToken = mockMvc.perform(
                post("/auth/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", notNullValue()))
                .andExpect(cookie().exists("refresh_token"))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getHeader("Authorization");

        mockMvc.perform(
                post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("로그아웃 성공"))
                .andExpect(header().doesNotExist("Authorization"))
                .andExpect(cookie().exists("refresh_token"))
                .andExpect(cookie().value("refresh_token", nullValue()))
                .andExpect(cookie().maxAge("refresh_token", 0))
                .andDo(print());
    }
}