package com.orbyta_admission_quiz.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.time.LocalDateTime;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Handle FabrickApiException")
    void handleFabrickApiException_shouldReturnCorrectResponse() {
        String errorMessage = "{\"errorCode\":\"1234\",\"errorMessage\":\"Detailed error message\"}";
        HttpStatus errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        FabrickApiException exception = new FabrickApiException(errorMessage, "Error communicating with Fabrick", 500);
        ResponseEntity<Map<String, Object>> responseEntity = globalExceptionHandler.handleFabrickApiException(exception);
        assertNotNull(responseEntity);
        assertEquals(errorStatus, responseEntity.getStatusCode());
        Map<String, Object> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("timestamp"));
        assertNotNull(responseBody.get("timestamp"));
        assertTrue(responseBody.get("timestamp") instanceof LocalDateTime);
        assertEquals("Fabrick API", responseBody.get("step"));
        assertEquals("Error communicating with Fabrick", responseBody.get("rawErrorMessage"));
        assertTrue(responseBody.containsKey("errorDetails"));
        Map<String, Object> errorDetails = (Map<String, Object>) responseBody.get("errorDetails");
        assertNotNull(errorDetails);
        assertEquals("1234", errorDetails.get("errorCode"));
        assertEquals("Detailed error message", errorDetails.get("errorMessage"));
    }

    @Test
    @DisplayName("Handle MethodArgumentNotValidException")
    void handleValidationExceptions_shouldReturnBadRequestWithErrors() throws NoSuchMethodException {
        Object targetObject = new Object();
        String objectName = "targetObjectName";
        BindingResult bindingResult = new BeanPropertyBindingResult(targetObject, objectName);
        bindingResult.addError(new FieldError(objectName, "field1", "must not be null"));
        bindingResult.addError(new FieldError(objectName, "field2", "size must be between 5 and 10"));
        MethodParameter parameter = new MethodParameter(
                this.getClass().getDeclaredMethod("dummyMethodForParameter", String.class), 0);

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);
        ResponseEntity<Map<String, Object>> responseEntity = globalExceptionHandler.handleValidationExceptions(exception);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        Map<String, Object> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("timestamp"));
        assertNotNull(responseBody.get("timestamp"));
        assertTrue(responseBody.get("timestamp") instanceof LocalDateTime);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody.get("status"));

        assertTrue(responseBody.containsKey("errors"));
        Object errorsObject = responseBody.get("errors");
        assertNotNull(errorsObject);
        assertTrue(errorsObject instanceof Map);

        @SuppressWarnings("unchecked")
        Map<String, String> errorsMap = (Map<String, String>) errorsObject;

        assertEquals(2, errorsMap.size());
        assertEquals("must not be null", errorsMap.get("field1"));
        assertEquals("size must be between 5 and 10", errorsMap.get("field2"));
    }

    @Test
    @DisplayName("Handle General Exception")
    void handleGeneralException_shouldReturnInternalServerError() {
        Exception exception = new Exception("Test exception message");
        ResponseEntity<Map<String, Object>> responseEntity = globalExceptionHandler.handleGeneralException(exception);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        Map<String, Object> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("timestamp"));
        assertNotNull(responseBody.get("timestamp"));
        assertTrue(responseBody.get("timestamp") instanceof LocalDateTime);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseBody.get("status"));
        assertEquals("An unexpected error occurred", responseBody.get("message"));
    }

    void dummyMethodForParameter(String param) { // This method is to create a MethodParameter instance
    }
}
