package com.gameet.match.api;

import com.gameet.global.annotation.AccessLoggable;
import com.gameet.global.dto.UserPrincipal;
import com.gameet.match.dto.request.MatchAppointmentRequest;
import com.gameet.match.dto.request.MatchConditionRequest;
import com.gameet.match.dto.response.MatchAppointmentResponse;
import com.gameet.match.dto.response.MatchStatusWithInfoResponse;
import com.gameet.match.enums.MatchStatus;
import com.gameet.match.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "매칭 시작")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "매칭 시작", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "이미 매칭 중입니다. or 이미 매칭이 완료된 상태입니다.", content = @Content(schema = @Schema(implementation = String.class))),
    })
    @AccessLoggable(action = "매칭 시작")
    @PostMapping
    public ResponseEntity<?> tryMatch(@RequestBody @Valid MatchConditionRequest matchConditionRequest,
                           @AuthenticationPrincipal UserPrincipal userPrincipal) {
        matchService.tryMatch(userPrincipal.getUserId(), matchConditionRequest);

        return ResponseEntity.ok(MatchStatus.SEARCHING);
    }

    @Operation(summary = "매칭 취소")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "매칭 취소", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "이미 매칭이 완료된 상태입니다.", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "락 획득 실패", content = @Content(schema = @Schema(implementation = String.class))),
    })
    @AccessLoggable(action = "매칭 취소")
    @DeleteMapping
    public ResponseEntity<?> cancelMatch(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        matchService.cancelMatch(userPrincipal.getUserId());

        return ResponseEntity.ok(MatchStatus.CANCEL);
    }

    @Operation(summary = "매칭 상태 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "매칭 상태 조회", content = @Content(schema = @Schema(implementation = String.class))),
    })
    @AccessLoggable(action = "매칭 상태 조회")
    @GetMapping
    public ResponseEntity<?> matchStatus(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        MatchStatusWithInfoResponse response = matchService.getMatchStatusWithInfo(userPrincipal.getUserId());

        return ResponseEntity.ok(response);
    }

    @AccessLoggable(action = "매칭 약속")
    @PostMapping("/match-appointment")
    public ResponseEntity<?> createMatchAppointment(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                    @RequestBody @Valid MatchAppointmentRequest matchAppointmentRequest) {
        MatchAppointmentResponse response = matchService.createMatchAppointment(userPrincipal.getUserId(), matchAppointmentRequest);
        return ResponseEntity.ok(response);
    }
}
