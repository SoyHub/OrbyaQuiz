package com.orbyta_admission_quiz.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.orbyta_admission_quiz.dto.errors.ApiError;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FabrickResponse<T>(
        String status,
        @JsonProperty("error") List<ApiError> errors,
        T payload
) { // To show the use of record feature
    public boolean isSuccess() {
        return "OK".equals(status);
    }
}