package com.stocktrading.exception;

/**
 * Thrown when attempting to register a username that is already taken.
 */
public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
