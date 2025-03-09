package com.madimadica.betterenv;

/**
 * Represents an exception originating from a programming error involving the type used in {@link BetterEnv#load(Class)}.
 */
public class InvalidEnvTypeException extends IllegalArgumentException {
    /**
     * Construct an exception with the given message
     * @param s error message
     */
    public InvalidEnvTypeException(String s) {
        super(s);
    }

    /**
     * Construct an exception with the given message and cause
     * @param message error message
     * @param cause cause
     */
    public InvalidEnvTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
