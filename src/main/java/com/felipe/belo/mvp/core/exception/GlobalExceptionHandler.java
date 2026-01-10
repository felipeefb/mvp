package com.felipe.belo.mvp.core.exception;

import java.rmi.AccessException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.felipe.belo.mvp.core.utils.I18nConstants;
import org.springframework.web.client.HttpServerErrorException;

/**
 * Global fallback exception handlers translating framework errors into business exceptions.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Default constructor. */
    public GlobalExceptionHandler() { }

    /**
     * Handles generic access violations by raising a business exception.
     */
    @ExceptionHandler(AccessException.class)
    public void handleException() {
        throw new BusinessException(HttpStatus.FORBIDDEN, I18nConstants.MESSAGE_USER_FORBIDDEN);
    }

    /**
     * Handles unexpected server errors by raising a generic business exception.
     */
    @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
    public void handleInternalServerError() {
        throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, I18nConstants.MESSAGE_INTERNAL_SERVER_ERROR);
    }
}