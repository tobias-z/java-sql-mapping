package com.tobias_z.exceptions;

public class NoPrimaryKeyFound extends RuntimeException {
    public NoPrimaryKeyFound(String message) {
        super(message);
    }
}
