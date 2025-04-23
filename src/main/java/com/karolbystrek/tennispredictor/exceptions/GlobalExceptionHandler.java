package com.karolbystrek.tennispredictor.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(PlayerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePlayerNotFoundException(PlayerNotFoundException ex) {
        log.warn("Handling PlayerNotFoundException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(PredictionServiceException.class)
    public ResponseEntity<ErrorResponse> handlePredictionServiceException(PredictionServiceException ex) {
        log.error("Handling PredictionServiceException (Status {}): {}", ex.getStatusCode(), ex.getMessage(), ex);
        return ResponseEntity.status(ex.getStatusCode())
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        log.error("Handling UserAlreadyExistsException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StructuredErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existingValue, newValue) -> existingValue
                ));
        log.warn("Handling MethodArgumentNotValidException: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StructuredErrorResponse(errors));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<StructuredErrorResponse> handleBindException(BindException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existingValue, newValue) -> existingValue
                ));
        log.warn("Handling BindException: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StructuredErrorResponse(errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Handling unexpected Exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("An unexpected internal error occurred. Please try again later."));
    }
}
