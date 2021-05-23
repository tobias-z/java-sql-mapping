package com.tobias_z.exceptions;

public class NoTableFound extends RuntimeException {
    public NoTableFound(String message) {
        super(message);
    }
}
