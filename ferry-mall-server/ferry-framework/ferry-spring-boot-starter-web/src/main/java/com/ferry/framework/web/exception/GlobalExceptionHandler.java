package com.ferry.framework.web.exception;

import com.ferry.framework.web.core.CommonResult;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(FerryBusinessException.class)
    public CommonResult<Void> handleBusiness(FerryBusinessException ex) {
        log.warn("business_exception code={} message={}", ex.getCode(), ex.getMessage());
        return CommonResult.failed(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class, HttpMessageNotReadableException.class})
    public CommonResult<Void> handleBadRequest(Exception ex) {
        log.warn("bad_request exception={}", ex.getClass().getSimpleName());
        return CommonResult.failed(400, "请求参数不正确");
    }

    @ExceptionHandler(Exception.class)
    public CommonResult<Void> handleException(Exception ex) {
        log.error("unhandled_exception", ex);
        return CommonResult.failed(500, "系统繁忙，请稍后再试");
    }
}
