package com.orbyta_admission_quiz.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FabrickApiException extends Exception {
    private final String message;
    private final String rawMessage;
    private final int httpStatus;
}
