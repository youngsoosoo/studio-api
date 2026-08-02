package com.studio.api.common;

/** Maps to HTTP 401 with error code UNAUTHORIZED (see GlobalExceptionHandler). */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
