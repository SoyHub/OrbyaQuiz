package com.orbyta_admission_quiz.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    @Mock
    private LoggingUtils loggingUtils;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private Signature signature;

    @InjectMocks
    private LoggingAspect loggingAspect;

    @BeforeEach
    void setUp() {
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringType()).thenReturn(Object.class);
        when(signature.getName()).thenReturn("testMethod");
        when(loggingUtils.generateArrows(anyString())).thenReturn(">>>");
    }

    @Test
    void logAroundWithDepth_shouldLogMethodEntryAndExit() throws Throwable {
        Object expectedResult = "test result";
        when(joinPoint.proceed()).thenReturn(expectedResult);
        Object result = loggingAspect.logAroundWithDepth(joinPoint);
        assertEquals(expectedResult, result);
        verify(loggingUtils).incrementDepth();
        verify(loggingUtils).cleanupDepth();
        verify(loggingUtils).logMethodEntry(joinPoint, "Object", "testMethod", ">>>");
        verify(loggingUtils).logMethodExit(eq(expectedResult), eq("Object"), eq("testMethod"), anyLong(), eq(">>>"));
    }

    @Test
    void logAroundWithDepth_shouldLogAndRethrowException() throws Throwable {
        RuntimeException expectedException = new RuntimeException("Something went wrong");
        when(joinPoint.proceed()).thenThrow(expectedException);
        Exception exception = assertThrows(RuntimeException.class, () -> {
            loggingAspect.logAroundWithDepth(joinPoint);
        });
        assertEquals(expectedException, exception);
        verify(loggingUtils).incrementDepth();
        verify(loggingUtils).cleanupDepth();
    }

    @Test
    void logAroundWithDepth_shouldHandleExecutionTimeCorrectly() throws Throwable {
        Object expectedResult = "test result";
        when(joinPoint.proceed()).thenReturn(expectedResult);
        Object result = loggingAspect.logAroundWithDepth(joinPoint);
        assertEquals(expectedResult, result);
        verify(loggingUtils).incrementDepth();
        verify(loggingUtils).cleanupDepth();
        verify(loggingUtils).logMethodEntry(any(), anyString(), anyString(), anyString());
        verify(loggingUtils).logMethodExit(any(), anyString(), anyString(), anyLong(), anyString());
    }
}