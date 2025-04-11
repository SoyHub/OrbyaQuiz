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
        when(loggingUtils.toJson(any())).thenReturn("{}");
    }

    @Test
    void logAroundWithDepth_shouldLogMethodEntryAndExit() throws Throwable {
        Object expectedResult = "test result";
        when(joinPoint.proceed()).thenReturn(expectedResult);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"testArg"});
        Object result = loggingAspect.logAroundWithDepth(joinPoint);
        assertEquals(expectedResult, result);
        verify(loggingUtils).incrementDepth();
        verify(loggingUtils).decrementDepth();
        verify(loggingUtils, times(2)).toJson(any());
        verify(loggingUtils).generateArrows(">");
        verify(loggingUtils).generateArrows("<");
    }

    @Test
    void logAroundWithDepth_shouldLogAndRethrowIllegalArgumentException() throws Throwable {
        IllegalArgumentException expectedException = new IllegalArgumentException("Invalid argument");
        when(joinPoint.proceed()).thenThrow(expectedException);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"testArg"});
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            loggingAspect.logAroundWithDepth(joinPoint);
        });
        assertEquals(expectedException, exception);
        verify(loggingUtils).incrementDepth();
        verify(loggingUtils).decrementDepth();
        verify(loggingUtils, times(1)).toJson(any());
        verify(loggingUtils).generateArrows(">");
        verify(loggingUtils).generateArrows("<");
    }

    @Test
    void logAroundWithDepth_shouldLogAndRethrowGenericException() throws Throwable {
        RuntimeException expectedException = new RuntimeException("Something went wrong");
        when(joinPoint.proceed()).thenThrow(expectedException);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"testArg"});
        Exception exception = assertThrows(RuntimeException.class, () -> {
            loggingAspect.logAroundWithDepth(joinPoint);
        });
        assertEquals(expectedException, exception);
        verify(loggingUtils).incrementDepth();
        verify(loggingUtils).decrementDepth();
        verify(loggingUtils, times(1)).toJson(any());
        verify(loggingUtils).generateArrows(">");
        verify(loggingUtils).generateArrows("<");
    }

    @Test
    void logAroundWithDepth_shouldRemoveDepthWhenFinalDepthIsZero() throws Throwable {
        when(joinPoint.proceed()).thenReturn("result");
        when(loggingUtils.decrementDepth()).thenReturn(0);
        loggingAspect.logAroundWithDepth(joinPoint);
        verify(loggingUtils).removeDepth();
    }

    @Test
    void logAroundWithDepth_shouldNotRemoveDepthWhenFinalDepthIsNotZero() throws Throwable {
        when(joinPoint.proceed()).thenReturn("result");
        when(loggingUtils.decrementDepth()).thenReturn(1);
        loggingAspect.logAroundWithDepth(joinPoint);
        verify(loggingUtils, never()).removeDepth();
    }

}