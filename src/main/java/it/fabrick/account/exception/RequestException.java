package it.fabrick.account.exception;

import lombok.Getter;

@Getter
public abstract class RequestException extends RuntimeException {

    private final String exceptionCode;

    protected RequestException(String message, String exceptionCode) {
        super(message);
        this.exceptionCode = exceptionCode;
    }
}
