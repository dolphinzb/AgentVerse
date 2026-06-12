package com.agentverse.common.dto;

import lombok.Generated;

import java.io.Serializable;

/**
 * 统一 API 响应包装。
 *
 * @param <T> 业务数据类型
 */
public class ApiResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer code;
    private String message;
    private T data;
    private Long timestamp;

    public ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public ApiResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /** 成功（无数据） */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "success", null);
    }

    /** 成功（带数据） */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    /** 成功（自定义消息 + 数据） */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    /** 失败（默认 500） */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, message, null);
    }

    /** 失败（自定义 code） */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    /** 失败（自定义 code + 数据） */
    public static <T> ApiResponse<T> error(Integer code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

    @Generated
    public Integer getCode() { return this.code; }

    @Generated
    public String getMessage() { return this.message; }

    @Generated
    public T getData() { return this.data; }

    @Generated
    public Long getTimestamp() { return this.timestamp; }

    @Generated
    public void setCode(Integer code) { this.code = code; }

    @Generated
    public void setMessage(String message) { this.message = message; }

    @Generated
    public void setData(T data) { this.data = data; }

    @Generated
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}
