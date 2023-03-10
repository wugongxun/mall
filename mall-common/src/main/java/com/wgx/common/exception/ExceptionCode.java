package com.wgx.common.exception;

import lombok.Data;
import org.apache.http.HttpStatus;

/**
 * @author wgx
 * @since 2023/3/7 16:13
 */
public enum ExceptionCode implements HttpStatus {
    UNKNOWN_EXCEPTION(10000, "未知异常"),
    VALIDATION_EXCEPTION(10001, "参数校验异常");

    private Integer code;
    private String message;

    ExceptionCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer code() {
        return code;
    }

    public String message() {
        return message;
    }
}
