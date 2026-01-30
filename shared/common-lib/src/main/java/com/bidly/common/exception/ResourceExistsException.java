package com.bidly.common.exception;

public class ResourceExistsException extends RuntimeException {
    public ResourceExistsException(String message) {
        super(message);
    }
}
