package com.gameet.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gameet.global.TestJwtUtil;
import com.gameet.auth.enums.Role;
import com.gameet.global.enums.GamePlatform;
import com.gameet.global.enums.GameSkillLevel;
import com.gameet.global.enums.PlayStyle;
import com.gameet.global.enums.PreferredGenre;
import com.gameet.user.dto.UserProfileUpdateRequest;
import com.gameet.user.service.UserService;
import com.gameet.user.dto.UserProfileRequest;
import com.gameet.user.entity.User;
import com.gameet.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("[유저 프로필 성공 테스트] 유저 프로필 생성하면 유저 + 유저 프로필 데이터 반환")
    public void save_user_profile_success_test() throws Exception {
        // given
        User user = User.builder()
                .role(Role.USER)
                .email("qw123@gmail.com")
                .password("password")
                .build();

        userRepository.save(user);

        UserProfileRequest userProfileRequest = UserProfileRequest.builder()
                .nickname("qw123")
                .age(25)
                .showAge(true)
                .gender("F")
                .preferredGenres(List.of(PreferredGenre.HORROR, PreferredGenre.FPS))
                .platforms(List.of(GamePlatform.VR, GamePlatform.CONSOLE))
                .playStyle(PlayStyle.CASUAL)
                .gameSkillLevel(GameSkillLevel.ADVANCED)
                .isAdultMatchAllowed(true)
                .isVoice(true)
                .build();

        String accessToken = TestJwtUtil.generateTestAccessToken(user.getUserId(), Role.USER);

        // when & then
        mockMvc.perform(
                post("/users/profile")
                        .content(objectMapper.writeValueAsString(userProfileRequest))
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user_id").isNumber())
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.nickname").value(userProfileRequest.nickname()))
                .andExpect(jsonPath("$.age").value(userProfileRequest.age()))
                .andExpect(jsonPath("$.show_age").value(userProfileRequest.showAge()))
                .andExpect(jsonPath("$.gender").value(userProfileRequest.gender()))
                .andExpect(jsonPath("$.manner_score").isNumber())
                .andExpect(jsonPath("$.preferred_genres", containsInAnyOrder(PreferredGenre.HORROR.toString(), PreferredGenre.FPS.toString())))
                .andExpect(jsonPath("$.game_platforms", containsInAnyOrder(GamePlatform.VR.toString(), GamePlatform.CONSOLE.toString())))
                .andExpect(jsonPath("$.play_style").value(userProfileRequest.playStyle().toString()))
                .andExpect(jsonPath("$.game_skill_level").value(userProfileRequest.gameSkillLevel().toString()))
                .andExpect(jsonPath("$.is_adult_match_allowed").value(userProfileRequest.isAdultMatchAllowed()))
                .andExpect(jsonPath("$.is_voice").value(userProfileRequest.isVoice()))
                .andDo(print());
    }

    @Test
    @DisplayName("[유저 프로필 실패 테스트] 유저 프로필이 존재하면 400 에러 발생")
    public void shouldReturn400_whenUserProfileAlreadyExists() throws Exception {
        // given
        User user = User.builder()
                .role(Role.USER)
                .email("qw134@gmail.com")
                .password("password")
                .build();

        userRepository.save(user);

        UserProfileRequest userProfileRequest = UserProfileRequest.builder()
                .nickname("qw134")
                .age(25)
                .showAge(true)
                .gender("F")
                .preferredGenres(List.of(PreferredGenre.HORROR, PreferredGenre.FPS))
                .platforms(List.of(GamePlatform.VR, GamePlatform.CONSOLE))
                .playStyle(PlayStyle.CASUAL)
                .gameSkillLevel(GameSkillLevel.ADVANCED)
                .isAdultMatchAllowed(true)
                .isVoice(true)
                .build();

        userService.saveUserProfile(user.getUserId(), userProfileRequest);

        String testAccessToken = TestJwtUtil.generateTestAccessToken(user.getUserId(), Role.USER);

        // when & then
        mockMvc.perform(
                post("/users/profile")
                        .content(objectMapper.writeValueAsString(userProfileRequest))
                        .header("Authorization", "Bearer " + testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("유저 프로필이 이미 존재합니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[유저 프로필 테스트] 기존 프로필이 있으면 업데이트 성공")
    void updateUserProfile_whenProfileExists_thenSuccess() throws Exception {
        // given
        User user = User.builder()
                .role(Role.USER)
                .email("qw124@gmail.com")
                .password("password")
                .build();

        userRepository.save(user);

        UserProfileRequest userProfileRequest = UserProfileRequest.builder()
                .nickname("qw124")
                .age(25)
                .showAge(true)
                .gender("F")
                .preferredGenres(List.of(PreferredGenre.HORROR, PreferredGenre.FPS))
                .platforms(List.of(GamePlatform.VR, GamePlatform.CONSOLE))
                .playStyle(PlayStyle.CASUAL)
                .gameSkillLevel(GameSkillLevel.ADVANCED)
                .isAdultMatchAllowed(true)
                .isVoice(true)
                .build();

        userService.saveUserProfile(user.getUserId(), userProfileRequest);

        UserProfileUpdateRequest userProfileUpdateRequest = UserProfileUpdateRequest.builder()
                .showAge(false)
                .preferredGenres(List.of(PreferredGenre.FPS))
                .platforms(List.of(GamePlatform.MOBILE))
                .build();

        String testAccessToken = TestJwtUtil.generateTestAccessToken(user.getUserId(), Role.USER);

        // when & then
        mockMvc.perform(
                put("/users/profile")
                        .content(objectMapper.writeValueAsString(userProfileUpdateRequest))
                        .header("Authorization", "Bearer " + testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").isNumber())
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.nickname").value(userProfileRequest.nickname()))
                .andExpect(jsonPath("$.show_age").value(userProfileUpdateRequest.showAge()))
                .andExpect(jsonPath("$.preferred_genres", containsInAnyOrder(PreferredGenre.FPS.toString())))
                .andExpect(jsonPath("$.game_platforms", containsInAnyOrder(GamePlatform.MOBILE.toString())))
                .andDo(print());
    }

    @Test
    @DisplayName("[유저 프로필 실패 테스트] 기존 프로필이 없으면 400 에러 반환")
    void updateUserProfile_whenProfileDoesNotExist_thenThrows400() throws Exception {
        // given
        User user = User.builder()
                .role(Role.USER)
                .email("qw1234@gmail.com")
                .password("password")
                .build();

        userRepository.save(user);

        UserProfileUpdateRequest userProfileUpdateRequest = UserProfileUpdateRequest.builder()
                .showAge(false)
                .preferredGenres(List.of(PreferredGenre.FPS))
                .platforms(List.of(GamePlatform.MOBILE))
                .build();

        String testAccessToken = TestJwtUtil.generateTestAccessToken(user.getUserId(), Role.USER);

        // when & then
        mockMvc.perform(
                put("/users/profile")
                        .content(objectMapper.writeValueAsString(userProfileUpdateRequest))
                        .header("Authorization", "Bearer " + testAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("유저 프로필이 존재하지 않습니다. 기본 프로필을 생성해주세요."))
                .andDo(print());
    }
}