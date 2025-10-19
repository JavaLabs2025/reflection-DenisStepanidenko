package org.example.exceptions;

public class NoConstructorsFoundException extends RuntimeException {

    public NoConstructorsFoundException(String message) {
        super(message);
    }
}
