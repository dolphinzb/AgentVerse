/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.exception;

import com.agentverse.common.exception.ErrorCode;
import lombok.Generated;

public class BizException
extends RuntimeException {
    private final Integer code;

    public BizException(String message) {
        super(message);
        this.code = 500;
    }

    public BizException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BizException(ErrorCode errorCode, String detail) {
        super(errorCode.getMessage() + ": " + detail);
        this.code = errorCode.getCode();
    }

    @Generated
    public Integer getCode() {
        return this.code;
    }
}

