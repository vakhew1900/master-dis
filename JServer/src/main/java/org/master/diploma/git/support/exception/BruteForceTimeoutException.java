package org.master.diploma.git.support.exception;

public class BruteForceTimeoutException extends RuntimeException{

    public BruteForceTimeoutException(String message) {
        super(message);
    }

    public BruteForceTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
