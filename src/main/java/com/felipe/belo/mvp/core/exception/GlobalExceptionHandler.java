package com.felipe.belo.mvp.core.exception;

import java.rmi.AccessException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.felipe.belo.mvp.core.utils.I18nConstants;
import org.springframework.web.client.HttpServerErrorException;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(AccessException.class)
    public void handleException() {
        throw new BusinessException(HttpStatus.FORBIDDEN, I18nConstants.MESSAGE_USER_FORBIDDEN);
    }

    @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
    public void handleInternalServerError() {
        throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, I18nConstants.MESSAGE_INTERNAL_SERVER_ERROR);
    }
}