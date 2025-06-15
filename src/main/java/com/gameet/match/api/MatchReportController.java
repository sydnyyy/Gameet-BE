package com.gameet.match.api;

import com.gameet.global.annotation.AccessLoggable;
import com.gameet.global.dto.UserPrincipal;
import com.gameet.match.dto.request.MatchReportRequest;
import com.gameet.match.service.MatchReportService;
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
@RequestMapping("/api/match/report")
public class MatchReportController {

    private final MatchReportService matchReportService;

    @AccessLoggable(action = "신고")
    @PostMapping
    public ResponseEntity<?> report(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                    @RequestBody @Valid MatchReportRequest matchReportRequest) {

        matchReportService.report(userPrincipal.getUserId(), matchReportRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
