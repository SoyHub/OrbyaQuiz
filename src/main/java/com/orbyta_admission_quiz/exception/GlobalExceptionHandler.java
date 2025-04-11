package com.orbyta_admission_quiz.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orbyta_admission_quiz.dto.errors.FabrickApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FabrickApiException.class)
    public ResponseEntity<FabrickApiErrorResponse> handleFabrickApiException(FabrickApiException ex) {
        log.error("Fabrick API error: {}", ex.getMessage(), ex);

        FabrickApiErrorResponse.FabrickApiErrorResponseBuilder responseBuilder = FabrickApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getHttpStatus())
                .method(ex.getStackTrace()[1].getMethodName())
                .rawErrorMessage(ex.getRawMessage());

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> errorDetails = objectMapper.readValue(ex.getMessage(), Map.class);
            responseBuilder.errorDetails(errorDetails);
        } catch (Exception e) {
            responseBuilder.message(ex.getMessage());
        }

        FabrickApiErrorResponse response = responseBuilder.build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<FabrickApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage(), ex);

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        FabrickApiErrorResponse response = FabrickApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed for one or more fields")
                .errorDetails(Map.of("validationErrors", errors))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<FabrickApiErrorResponse> handleGeneralException(Exception ex) {
        log.error("General error: {}", ex.getMessage(), ex);

        FabrickApiErrorResponse response = FabrickApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An unexpected error occurred")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<FabrickApiErrorResponse> handleMissingParams(MissingServletRequestParameterException ex) {
        log.error("Missing parameter: {}", ex.getMessage(), ex);

        FabrickApiErrorResponse response = FabrickApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Missing required parameter: " + ex.getParameterName())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
