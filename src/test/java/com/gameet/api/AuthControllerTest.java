package com.gameet.api;

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

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
                .username("username-123")
                .password("password")
                .fullName("fullName")
                .gender("F")
                .age(23)
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
    @DisplayName("[회원가입 성공 테스트] 회원가입 성공하면 사용자 기본 정보 반환")
    public void sign_up_success_returns_basic_user_info() throws Exception {
        // given
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username("username-123")
                .password("password")
                .fullName("fullName")
                .gender("F")
                .age(23)
                .build();

        // when & then
        mockMvc.perform(
                post("/auth/sign-up/user")
                        .content(objectMapper.writeValueAsString(signUpRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").isNumber())
                .andExpect(jsonPath("$.username").value(signUpRequest.username()))
                .andExpect(jsonPath("$.fullName").value(signUpRequest.fullName()))
                .andExpect(jsonPath("$.gender").value(signUpRequest.gender()))
                .andExpect(jsonPath("$.age").value(signUpRequest.age()))
                .andDo(print());
    }

    @Test
    @DisplayName("[회원가입 ROLE(USER) 테스트] /sign-up/user 로 요청 시 ROLE=USER")
    public void user_sign_up_should_assign_user_role() throws Exception {
        // given
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username("username-345")
                .password("password")
                .fullName("fullName")
                .gender("F")
                .age(23)
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

//        Optional<User> user = userRepository.findByUsername(signUpRequest.username());
//        assertTrue(user.isPresent());
//        assertEquals(Role.USER, user.get().getRole());
    }

    @Test
    @DisplayName("[회원가입 ROLE(ADMIN) 테스트] /sign-up/admin 로 요청 시 ROLE=ADMIN")
    public void admin_sign_up_should_assign_admin_role() throws Exception{
        // given
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username("username-321")
                .password("password")
                .fullName("fullName")
                .gender("F")
                .age(23)
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

        Optional<User> user = userRepository.findByUsername(signUpRequest.username());
        assertTrue(user.isPresent());
        assertEquals(Role.ADMIN, user.get().getRole());
    }

    @Test
    @DisplayName("[회원가입 유효성 테스트] 유저이름 필수")
    public void sign_up_should_fail_when_username_is_blank() throws Exception {
        // given
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username(" ")
                .password("password")
                .fullName("fullName")
                .gender("F")
                .age(23)
                .build();

        // when & then
        mockMvc.perform(
                post("/auth/sign-up/user")
                        .content(objectMapper.writeValueAsString(signUpRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("유저이름은 필수입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[회원가입 유효성 테스트] 성별 F, M, N 만 가능")
    public void sign_up_should_fail_when_gender_is_invalid() throws Exception {
        // given
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username("username-432")
                .password("password")
                .fullName("fullName")
                .gender("A")
                .age(23)
                .build();

        // when & then
        mockMvc.perform(
                post("/auth/sign-up/user")
                        .content(objectMapper.writeValueAsString(signUpRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("성별은 F, M, N 중 하나여야 합니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[회원가입 유효성 테스트] 나이는 1살 이상만 가능")
    public void sign_up_should_fail_when_birth_format_is_invalid() throws Exception {
        // given
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username("username-143")
                .password("password")
                .fullName("fullName")
                .gender("F")
                .age(0)
                .build();

        // when & then
        mockMvc.perform(
                post("/auth/sign-up/user")
                        .content(objectMapper.writeValueAsString(signUpRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("나이는 1살 이상이어야 합니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[로그인 성공 테스트] 로그인 성공하면 JWT 발급")
    public void login_success_test() throws Exception {
        // given
        User user = User.builder()
                .role(Role.USER)
                .username("username-4321")
                .password("password")
                .fullName("fullName")
                .gender('F')
                .age(23)
                .build();

        userRepository.save(user);

        LoginRequest loginRequest = LoginRequest.builder()
                .username(user.getUsername())
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
    @DisplayName("[로그인 유효성 테스트] 유저이름 필수")
    public void login_should_fail_when_username_is_blank() throws Exception {
        // given
        LoginRequest loginRequest = LoginRequest.builder()
                .username("")
                .password("password")
                .build();

        // when & then
        mockMvc.perform(
                post("/auth/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("유저이름은 필수입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[로그아웃 성공 테스트] 로그아웃 시 Access Token 제거 & Refresh Token 만료")
    public void logout_success_test () throws Exception {
        // given
        User user = User.builder()
                .role(Role.USER)
                .username("username-321")
                .password("password")
                .fullName("fullName")
                .gender('F')
                .age(23)
                .build();

        userRepository.save(user);

        LoginRequest loginRequest = LoginRequest.builder()
                .username(user.getUsername())
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
                .andExpect(jsonPath("$.username").value(user.getUsername()))
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