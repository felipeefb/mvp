package com.felipe.belo.mvp.core.exception;

import com.felipe.belo.mvp.core.component.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;

/**
 * Global exception handler that translates application exceptions into consistent HTTP responses.
 */
@ControllerAdvice
public class ApiExceptionHandler {

    private final MessageService messages;

    /**
     * Creates a new handler.
     *
     * @param messages the message service used to resolve i18n messages
     */
    public ApiExceptionHandler(MessageService messages) {
        this.messages = messages;
    }

    /**
     * Handles domain/business exceptions.
     *
     * @param ex      the thrown business exception
     * @param request the web request context
     * @return a response entity containing an error body and the proper HTTP status
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex,
            WebRequest request
    ) {
        String message = messages.getMessage(ex.getMessageKey(), ex.getArgs());

        ErrorResponse body = new ErrorResponse(
                Instant.now().toString(),
                ex.getStatus().value(),
                ex.getStatus().getReasonPhrase(),
                ex.getMessageKey(),
                message
        );

        return ResponseEntity.status(ex.getStatus()).body(body);
    }


    /**
     * Fallback handler for uncaught exceptions.
     *
     * @param ex      the thrown exception
     * @param request the web request context
     * @return an internal server error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            WebRequest request
    ) {
        String message = ex.getMessage();

        ErrorResponse body = new ErrorResponse(
                Instant.now().toString(),
                500,
                "Internal Server Error",
                "error.internal",
                message
        );

        return ResponseEntity.status(500).body(body);
    }

    /**
     * Standard error payload returned by the API.
     *
     * @param timestamp ISO-8601 timestamp when the error occurred
     * @param status    HTTP status code
     * @param error     HTTP reason phrase
     * @param code      stable application error code
     * @param message   localized error message
     */
    public record ErrorResponse(
            String timestamp,
            int status,
            String error,
            String code,
            String message
    ) {}
}