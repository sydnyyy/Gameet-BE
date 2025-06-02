package com.gameet.match.api;

import com.gameet.global.annotation.AccessLoggable;
import com.gameet.global.dto.UserPrincipal;
import com.gameet.match.dto.request.MatchAppointmentRequest;
import com.gameet.match.dto.request.MatchConditionRequest;
import com.gameet.match.dto.response.MatchAppointmentResponse;
import com.gameet.match.dto.response.MatchStatusWithInfoResponse;
import com.gameet.match.enums.MatchStatus;
import com.gameet.match.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/match")
public class MatchController {

    private final MatchService matchService;

    @AccessLoggable(action = "매칭 시작")
    @PostMapping
    public ResponseEntity<?> tryMatch(@RequestBody @Valid MatchConditionRequest matchConditionRequest,
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
        MatchStatusWithInfoResponse response = matchService.getMatchStatusWithInfo(userPrincipal.getUserId());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/match-appointment")
    public ResponseEntity<?> createMatchAppointment(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                    @RequestBody @Valid MatchAppointmentRequest matchAppointmentRequest) {
        MatchAppointmentResponse response = matchService.createMatchAppointment(userPrincipal.getUserId(), matchAppointmentRequest);
        return ResponseEntity.ok(response);
    }
}
