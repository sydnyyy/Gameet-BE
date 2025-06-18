package com.gameet.common.api;

import com.gameet.common.enums.CodeGroup;
import com.gameet.common.service.CodeService;
import com.gameet.global.annotation.AccessLoggable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/common")
@Validated
public class CommonController {

    private final CodeService codeService;

    @Operation(summary = "공통 코드 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "공통 코드 조회", content = @Content(schema = @Schema(implementation = String.class))),
    })
    @AccessLoggable(action = "공통 코드 조회")
    @RequestMapping("/code")
    public ResponseEntity<?> getCommonCode(@RequestParam CodeGroup codeGroup) {
        Map<String, Map<String, String>> commonCode = codeService.getCommonCode(codeGroup);
        return ResponseEntity.ok(commonCode);
    }
}
