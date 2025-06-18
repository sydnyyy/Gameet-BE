package com.gameet.common.enums.base;

public interface BaseCodeEnum {
    public String getCode();
    public String getName();
    default Integer getSortOrder() {
        return null;
    }
}
