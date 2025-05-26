package com.gameet.common.service;

import com.gameet.common.enums.*;
import com.gameet.common.util.CodeUtil;
import com.gameet.match.enums.MannerEvaluation;
import com.gameet.match.enums.ReportReason;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CodeService {

    public Map<String, Map<String, String>> getCommonCode(CodeGroup codeGroup) {
        Map<String, Map<String, String>> result = new HashMap<>();

        switch (codeGroup) {
            case MATCH_CONDITION -> {
                result.put(CodeGroup.PREFERRED_GENRE.getCode(), CodeUtil.convertEnumToMap(PreferredGenre.class));
                result.put(CodeGroup.GAME_PLATFORM.getCode(), CodeUtil.convertEnumToMap(GamePlatform.class));
                result.put(CodeGroup.GAME_SKILL_LEVEL.getCode(), CodeUtil.convertEnumToMap(GameSkillLevel.class));
                result.put(CodeGroup.PLAY_STYLE.getCode(), CodeUtil.convertEnumToMap(PlayStyle.class));
            }
            case PREFERRED_GENRE -> {
                result.put(CodeGroup.PREFERRED_GENRE.getCode(), CodeUtil.convertEnumToMap(PreferredGenre.class));
            }
            case GAME_PLATFORM -> {
                result.put(CodeGroup.GAME_PLATFORM.getCode(), CodeUtil.convertEnumToMap(GamePlatform.class));
            }
            case GAME_SKILL_LEVEL -> {
                result.put(CodeGroup.GAME_SKILL_LEVEL.getCode(), CodeUtil.convertEnumToMap(GameSkillLevel.class));
            }
            case PLAY_STYLE -> {
                result.put(CodeGroup.PLAY_STYLE.getCode(), CodeUtil.convertEnumToMap(PlayStyle.class));
            }
            case REPORT_REASON -> {
                result.put(CodeGroup.REPORT_REASON.getCode(), CodeUtil.convertEnumToMap(ReportReason.class));
            }
            case MANNER_EVALUATION -> {
                result.put(CodeGroup.MANNER_EVALUATION.getCode(), CodeUtil.convertEnumToMap(MannerEvaluation.class));
            }
        }
        return result;
    }
}
