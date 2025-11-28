package com.felipe.belo.mvp.core.exception;

import com.felipe.belo.mvp.core.component.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;

@ControllerAdvice
public class ApiExceptionHandler {

    private final MessageService messages;

    public ApiExceptionHandler(MessageService messages) {
        this.messages = messages;
    }

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

    public record ErrorResponse(
            String timestamp,
            int status,
            String error,
            String code,
            String message
    ) {}
}