package com.hurryclear.usercenterbackend.common;

import com.fasterxml.jackson.databind.ser.Serializers;

public class ResultUtils {

    public static <T> BaseResponse<T> success(T data) {

        return new BaseResponse<>(0, data, "ok");
    }

    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse(errorCode);
    }

    public static BaseResponse error(ErrorCode errorCode, String message, String description) {
        return new BaseResponse(errorCode.getCode(), message, description);
    }

    public static BaseResponse error(ErrorCode errorCode, String description) {
        return new BaseResponse(errorCode.getCode(), null, errorCode.getMessage(), description);
    }

    public static BaseResponse error(int code, String message, String description) {
        return new BaseResponse(code, null, message, description);
    }
}