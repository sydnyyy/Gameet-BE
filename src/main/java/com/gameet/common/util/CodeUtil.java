package com.gameet.common.util;

import com.gameet.common.enums.base.BaseCodeEnum;

import java.util.EnumSet;
import java.util.Map;
import java.util.stream.Collectors;

public class CodeUtil {
    public static <E extends Enum<E> & BaseCodeEnum> Map<String, String> convertEnumToMap(Class<E> enumClass) {
        return EnumSet.allOf(enumClass).stream()
                .collect(Collectors.toMap(
                        BaseCodeEnum::getCode,
                        BaseCodeEnum::getName
                ));
    }
}
