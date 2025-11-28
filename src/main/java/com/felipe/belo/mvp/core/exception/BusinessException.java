package com.felipe.belo.mvp.core.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * A custom exception class representing business logic errors in the application.
 * <p>
 * This exception should be used to indicate scenarios where a specific business rule
 * has been violated, and it provides additional metadata such as HTTP status, message
 * key, and arguments for localization or detailed error representation.
 * <p>
 * It is designed to work seamlessly with exception handling mechanisms in the application,
 * allowing for consistent error responses and internationalization support.
 */
public class BusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
    private final HttpStatus status;
    private final String messageKey;
    private final Object[] args;


    private static final Logger logger = LoggerFactory.getLogger(BusinessException.class);

    public BusinessException(HttpStatus status, String messageKey, Object... args) {
        super(messageKey);
        this.status = status;
        this.messageKey = messageKey;
        this.args = args;

    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public Object[] getArgs() {
        return args;
    }
}