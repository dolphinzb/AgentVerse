/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.controller;

import com.agentverse.common.dto.ApiResponse;
import com.agentverse.common.exception.BizException;
import com.agentverse.common.exception.ErrorCode;
import java.util.stream.Collectors;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value={BizException.class})
    public ResponseEntity<ApiResponse<Void>> handleBizException(BizException e) {
        log.error("Business exception: code={}, message={}", (Object)e.getCode(), (Object)e.getMessage());
        return ResponseEntity.status((int)e.getCode()).body(ApiResponse.error(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(value={MethodArgumentNotValidException.class})
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("; "));
        log.error("Validation exception: {}", (Object)message);
        return ResponseEntity.status((HttpStatusCode)HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.PARAMETER_INVALID.getCode(), message));
    }

    @ExceptionHandler(value={BindException.class})
    public ResponseEntity<ApiResponse<Void>> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("; "));
        log.error("Bind exception: {}", (Object)message);
        return ResponseEntity.status((HttpStatusCode)HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.PARAMETER_INVALID.getCode(), message));
    }

    @ExceptionHandler(value={Exception.class})
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unexpected exception", (Throwable)e);
        return ResponseEntity.status((HttpStatusCode)HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(ErrorCode.INTERNAL_ERROR.getCode(), "Internal server error"));
    }
}

