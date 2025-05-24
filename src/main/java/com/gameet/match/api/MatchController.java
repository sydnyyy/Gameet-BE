package com.gameet.match.api;

import com.gameet.global.dto.UserPrincipal;
import com.gameet.match.dto.request.MatchConditionRequest;
import com.gameet.match.enums.MatchStatus;
import com.gameet.match.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/match")
public class MatchController {

    private final MatchService matchService;

    @PostMapping
    public ResponseEntity<?> tryMatch(@RequestBody MatchConditionRequest matchConditionRequest,
                           @AuthenticationPrincipal UserPrincipal userPrincipal) {
        matchService.tryMatch(userPrincipal.getUserId(), matchConditionRequest);

        return ResponseEntity.ok("매칭 시작");
    }

    @DeleteMapping
    public ResponseEntity<?> cancelMatch(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        matchService.cancelMatch(userPrincipal.getUserId());

        return ResponseEntity.ok("매칭 취소");
    }

    @GetMapping
    public ResponseEntity<?> matchStatus(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        MatchStatus matchStatus = matchService.getMatchStatus(userPrincipal.getUserId());

        return ResponseEntity.ok(matchStatus);
    }
}
