package com.wgx.mall.product.exception;

import com.wgx.common.exception.ExceptionCode;
import com.wgx.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wgx
 * @since 2023/3/7 15:58
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.wgx.mall.product.controller")
public class ExceptionControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> map = new HashMap<>();
        e.getFieldErrors().forEach(item -> map.put(item.getField(), item.getDefaultMessage()));
        return R.error(ExceptionCode.VALIDATION_EXCEPTION.code(), ExceptionCode.VALIDATION_EXCEPTION.message()).put("data", map);
    }

    @ExceptionHandler({Throwable.class})
    public R handleThrowable() {
        return R.error(ExceptionCode.UNKNOWN_EXCEPTION.code(), ExceptionCode.UNKNOWN_EXCEPTION.message());
    }
}
