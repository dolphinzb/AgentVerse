/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.model.dto;

import lombok.Generated;

public class ConnectionTestResult {
    private boolean success;
    private String message;

    public static ConnectionTestResult ok(String message) {
        ConnectionTestResult r = new ConnectionTestResult();
        r.setSuccess(true);
        r.setMessage(message);
        return r;
    }

    public static ConnectionTestResult fail(String message) {
        ConnectionTestResult r = new ConnectionTestResult();
        r.setSuccess(false);
        r.setMessage(message);
        return r;
    }

    @Generated
    public ConnectionTestResult() {
    }

    @Generated
    public boolean isSuccess() {
        return this.success;
    }

    @Generated
    public String getMessage() {
        return this.message;
    }

    @Generated
    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Generated
    public void setMessage(String message) {
        this.message = message;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ConnectionTestResult)) {
            return false;
        }
        ConnectionTestResult other = (ConnectionTestResult)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.isSuccess() != other.isSuccess()) {
            return false;
        }
        String this$message = this.getMessage();
        String other$message = other.getMessage();
        return !(this$message == null ? other$message != null : !this$message.equals(other$message));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof ConnectionTestResult;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isSuccess() ? 79 : 97);
        String $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "ConnectionTestResult(success=" + this.isSuccess() + ", message=" + this.getMessage() + ")";
    }
}

