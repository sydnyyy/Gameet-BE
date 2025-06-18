package com.gameet.match.api;

import com.gameet.global.annotation.AccessLoggable;
import com.gameet.global.dto.UserPrincipal;
import com.gameet.match.dto.request.MatchMannerEvaluationRequest;
import com.gameet.match.service.MatchMannerEvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/match/manner-evaluation")
public class MatchMannerEvaluationController {

    private final MatchMannerEvaluationService matchMannerEvaluationService;

    @Operation(summary = "매너 평가")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "매너 평가 성공", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "매칭룸이 존재하지 않습니다. or 이미 매너 평가가 완료되어 추가 평가가 불가능합니다.", content = @Content(schema = @Schema(implementation = String.class))),
    })
    @AccessLoggable(action = "매너 평가")
    @PostMapping
    public ResponseEntity<?> report(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                    @RequestBody @Valid MatchMannerEvaluationRequest matchMannerEvaluationRequest) {

        matchMannerEvaluationService.mannerEvaluation(userPrincipal.getUserId(), matchMannerEvaluationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
