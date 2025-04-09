package com.orbyta_admission_quiz.dto.errors;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FabrickApiErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String method;
    private Map<String, Object> errorDetails;
    private String message;
    private String rawErrorMessage;
    private Integer httpStatus;
}