package ru.kalampus.RequestService.exceptions;

public class ExternalSystemException extends RuntimeException {

    public ExternalSystemException() {
    }

    public ExternalSystemException(String message) {
        super(message);
    }

    public ExternalSystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExternalSystemException(Throwable cause) {
        super(cause);
    }

    public ExternalSystemException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
