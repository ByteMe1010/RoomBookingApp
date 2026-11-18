package com.codehack.nngf.Core.exceptions;

public class IDduplicationException extends RuntimeException {
    public IDduplicationException(String message) {
        super("ID's in Document Conflicts,Use Different ID. " + message);
    }
}
