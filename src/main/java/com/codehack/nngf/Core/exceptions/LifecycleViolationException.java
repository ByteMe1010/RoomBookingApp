package com.codehack.nngf.Core.exceptions;

public class LifecycleViolationException extends RuntimeException {
    public LifecycleViolationException(String message) {
        super("Lifecycle violation; calling DOM Critical Codes inside non Attached Element\n(FIX : Attach element to valid window or run code in active window. code you're expect to execue depends on dom and cannot be executed without connection with Window)\n" + message);
    }
}
