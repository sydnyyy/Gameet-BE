package com.gameet.match.api;

import com.gameet.global.annotation.AccessLoggable;
import com.gameet.global.dto.UserPrincipal;
import com.gameet.match.dto.request.MatchMannerEvaluationRequest;
import com.gameet.match.service.MatchMannerEvaluationService;
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

    @AccessLoggable(action = "매너 평가")
    @PostMapping
    public ResponseEntity<?> report(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                    @RequestBody @Valid MatchMannerEvaluationRequest matchMannerEvaluationRequest) {

        matchMannerEvaluationService.mannerEvaluation(userPrincipal.getUserId(), matchMannerEvaluationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
