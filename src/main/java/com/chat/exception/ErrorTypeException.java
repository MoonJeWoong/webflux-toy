package com.chat.exception;

import java.io.Serial;

public class ErrorTypeException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -7251103027650433836L;

    private final CustomErrorType errorType;

    public ErrorTypeException(String message, CustomErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    public CustomErrorType getErrorType() {
        return this.errorType;
    }

    @Override
    public String getMessage() {
        return "Code: " + errorType.getCode() + ", Message : " + super.getMessage();
    }
}
