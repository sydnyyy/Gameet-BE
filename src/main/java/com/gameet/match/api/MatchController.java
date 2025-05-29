package com.gameet.match.api;

import com.gameet.global.annotation.AccessLoggable;
import com.gameet.match.dto.request.MatchAppointmentRequest;
import com.gameet.match.dto.response.MatchAppointmentResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gameet.global.dto.UserPrincipal;
import com.gameet.match.dto.request.MatchConditionRequest;
import com.gameet.match.enums.MatchStatus;
import com.gameet.match.service.MatchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/match")
public class MatchController {

    private final MatchService matchService;

    @AccessLoggable(action = "매칭 시작")
    @PostMapping
    public ResponseEntity<?> tryMatch(@RequestBody MatchConditionRequest matchConditionRequest,
                           @AuthenticationPrincipal UserPrincipal userPrincipal) {
        matchService.tryMatch(userPrincipal.getUserId(), matchConditionRequest);

        return ResponseEntity.ok(MatchStatus.SEARCHING);
    }

    @AccessLoggable(action = "매칭 취소")
    @DeleteMapping
    public ResponseEntity<?> cancelMatch(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        matchService.cancelMatch(userPrincipal.getUserId());

        return ResponseEntity.ok(MatchStatus.CANCEL);
    }

    @AccessLoggable(action = "매칭 상태 조회")
    @GetMapping
    public ResponseEntity<?> matchStatus(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        MatchStatus matchStatus = matchService.getMatchStatus(userPrincipal.getUserId());

        return ResponseEntity.ok(matchStatus);
    }

    @PostMapping("/match-appointment")
    public ResponseEntity<?> createMatchAppointment(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                    @RequestBody @Valid MatchAppointmentRequest matchAppointmentRequest) {
        MatchAppointmentResponse response = matchService.createMatchAppointment(userPrincipal.getUserId(), matchAppointmentRequest);
        return ResponseEntity.ok(response);
    }
}
