package com.studio.api.common;

/** Maps to HTTP 404 with error code NOT_FOUND (see GlobalExceptionHandler). */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
