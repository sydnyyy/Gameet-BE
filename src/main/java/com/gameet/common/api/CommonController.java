package com.gameet.common.api;

import com.gameet.common.enums.CodeGroup;
import com.gameet.common.service.CodeService;
import com.gameet.global.annotation.AccessLoggable;
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

    @AccessLoggable(action = "공통 코드 조회")
    @RequestMapping("/code")
    public ResponseEntity<?> getCommonCode(@RequestParam CodeGroup codeGroup) {
        Map<String, Map<String, String>> commonCode = codeService.getCommonCode(codeGroup);
        return ResponseEntity.ok(commonCode);
    }
}
