package com.nayibit.lifeguide.common.exception;

/**
 * Base exception for all business/application errors.
 * Carries an {@link ErrorCode} that drives the HTTP status and the "code"
 * field of the JSON error response. The message defaults to the error
 * code's default message but can be overridden per throw-site, so a single
 * code can read differently in different endpoints without a new subclass.
 */
public class AppException extends RuntimeException {

    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    public AppException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AppException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
