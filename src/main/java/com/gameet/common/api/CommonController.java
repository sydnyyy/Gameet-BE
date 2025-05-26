package com.gameet.common.api;

import com.gameet.common.enums.CodeGroup;
import com.gameet.common.service.CodeService;
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

    @RequestMapping("/code")
    public ResponseEntity<?> getCommonCode(@RequestParam CodeGroup codeGroup) {
        Map<String, Map<String, String>> commonCode = codeService.getCommonCode(codeGroup);
        return ResponseEntity.ok(commonCode);
    }
}
