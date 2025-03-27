package com.hurryclear.usercenterbackend.common;

public enum ErrorCode {

    SUCCESS(0, "ok", ""),
    PARAMS_ERROR(40000, "request parameter error", ""),
    NULL_ERROR(40001, "request data is null", ""),
    NOT_LOGIN(40100, "not login", ""),
    NO_AUTH(40101, "no authority", ""),
    SYSTEM_ERROR(50000, "system error", "");


    private final int code;
    private final String message;
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getMessage() {
        return message;
    }
}
