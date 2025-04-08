package com.orbyta_admission_quiz.dto.errors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiError {
    private String status;
    private Object payload;

    @Data
    public static class ErrorDetail {
        private String code;
        private String description;
        private String params;
    }
}