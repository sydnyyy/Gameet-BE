package com.gameet.common.util;

import com.gameet.common.enums.base.BaseCodeEnum;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CodeUtil {
    public static <E extends Enum<E> & BaseCodeEnum> Map<String, String> convertEnumToMap(Class<E> enumClass) {
        return EnumSet.allOf(enumClass).stream()
            .sorted(Comparator.comparing(
                e -> Optional.ofNullable(e.getSortOrder()).orElse(Integer.MAX_VALUE)
            ))
            .collect(Collectors.toMap(
                BaseCodeEnum::getCode,
                BaseCodeEnum::getName,
                (v1, v2) -> v2,
                LinkedHashMap::new
            ));
    }

}
