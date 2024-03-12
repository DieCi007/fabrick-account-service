package it.fabrick.account.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class ThirdPartyException extends RequestException {
    @AllArgsConstructor
    @Getter
    public enum ExceptionSource {
        FABRICK
    }

    private final HttpStatusCode httpStatus;

    private final ExceptionSource exceptionSource;

    public ThirdPartyException(String message, ExceptionSource exceptionSource, String errorCode, HttpStatusCode httpStatus) {
        super(message, errorCode);
        this.exceptionSource = exceptionSource;
        this.httpStatus = httpStatus;
    }

}
