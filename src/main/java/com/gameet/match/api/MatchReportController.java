package com.gameet.match.api;

import com.gameet.global.annotation.AccessLoggable;
import com.gameet.global.dto.UserPrincipal;
import com.gameet.match.dto.request.MatchReportRequest;
import com.gameet.match.service.MatchReportService;
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
@RequestMapping("/api/match/report")
public class MatchReportController {

    private final MatchReportService matchReportService;

    @Operation(summary = "신고")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "신고 성공", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "매칭룸이 존재하지 않습니다. or 이미 신고가 완료되어 추가 신고가 불가능합니다.", content = @Content(schema = @Schema(implementation = String.class))),
    })
    @AccessLoggable(action = "신고")
    @PostMapping
    public ResponseEntity<?> report(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                    @RequestBody @Valid MatchReportRequest matchReportRequest) {

        matchReportService.report(userPrincipal.getUserId(), matchReportRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
