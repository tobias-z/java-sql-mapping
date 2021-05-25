package io.github.tobias_z.exceptions;

public class NoGeneratedKeyFound extends RuntimeException {
    public NoGeneratedKeyFound(String message) {
        super(message);
    }
}
