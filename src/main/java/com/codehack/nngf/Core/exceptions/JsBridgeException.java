package com.codehack.nngf.Core.exceptions;

public class JsBridgeException extends RuntimeException {
    public JsBridgeException(String message) {
        super(message);
    }

    public JsBridgeException(String message, Throwable cause) {
        super(message, cause);
    }
}
