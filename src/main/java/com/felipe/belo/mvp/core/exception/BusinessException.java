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
    /** HTTP status to return. */
    private final HttpStatus status;
    /** i18n message key associated with the error. */
    private final String messageKey;
    /** Optional arguments for message formatting. */
    private final Object[] args;


    private static final Logger logger = LoggerFactory.getLogger(BusinessException.class);

    /**
     * Creates a new business exception.
     *
     * @param status     the HTTP status to return
     * @param messageKey the i18n message key describing the error
     * @param args       optional formatting arguments for the message
     */
    public BusinessException(HttpStatus status, String messageKey, Object... args) {
        super(messageKey);
        this.status = status;
        this.messageKey = messageKey;
        this.args = args;

    }

    /**
     * Returns the HTTP status associated with this error.
     *
     * @return the status
     */
    public HttpStatus getStatus() {
        return status;
    }

    /**
     * Returns the i18n message key for this error.
     *
     * @return the message key
     */
    public String getMessageKey() {
        return messageKey;
    }

    /**
     * Returns the optional formatting arguments for the error message.
     *
     * @return message arguments
     */
    public Object[] getArgs() {
        return args;
    }
}