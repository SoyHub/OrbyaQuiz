package com.orbyta_admission_quiz.dto.account.response;

import com.orbyta_admission_quiz.dto.FabrickResponse;
import com.orbyta_admission_quiz.dto.errors.ApiError;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FabrickResponseTest {

    @Test
    void testIsSuccessWhenStatusIsOK() {
        FabrickResponse<String> response = new FabrickResponse<>("OK", null, "Payload");
        assertTrue(response.isSuccess(), "isSuccess should return true when status is 'OK'");
    }

    @Test
    void testIsSuccessWhenStatusIsNotOK() {
        FabrickResponse<String> response = new FabrickResponse<>("ERROR", null, "Payload");
        assertFalse(response.isSuccess(), "isSuccess should return false when status is not 'OK'");
    }

    @Test
    void testGettersAndSetters() {
        List<ApiError> errors = Collections.singletonList(new ApiError("ErrorCode", "ErrorMessage"));
        FabrickResponse<String> response = new FabrickResponse<>("OK", errors, "Payload");
        assertEquals("OK", response.status());
        assertEquals(errors, response.errors());
        assertEquals("Payload", response.payload());
    }
}