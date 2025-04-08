package com.ly.smartdoc.common;

public enum ErrorCoedEnum {
    PROJECT_ERROR("error", "can't get project info, please ensure the project has opened"),

    CONFIG_ERROR("config error", "can't load config file"),
    JSON_ERROR("json error", "config file must be json");
    private final String code;
    private final String message;

    ErrorCoedEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
