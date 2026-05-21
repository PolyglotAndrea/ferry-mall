package com.ferry.framework.web.exception;

public class FerryBusinessException extends RuntimeException {
    private final int code;

    public FerryBusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
