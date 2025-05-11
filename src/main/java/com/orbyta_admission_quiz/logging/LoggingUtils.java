package com.orbyta_admission_quiz.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingUtils {

    private final ObjectMapper objectMapper;
    private static final ThreadLocal<Integer> callDepth = ThreadLocal.withInitial(() -> 0);

    public void incrementDepth() {
        callDepth.set(callDepth.get() + 1);
    }

    public int decrementDepth() {
        int currentDepth = callDepth.get();
        callDepth.set(Math.max(0, currentDepth - 1));
        return callDepth.get();
    }

    public int getCurrentDepth() {
        return callDepth.get();
    }

    public void removeDepth() {
        callDepth.remove();
    }

    public void cleanupDepth() {
        if (decrementDepth() == 0) removeDepth();
    }

    public String generateArrows(String arrowChar) {
        return arrowChar.repeat(Math.max(1, getCurrentDepth()));
    }

    public String toJson(Object obj) {
        if (obj == null) return "null";

        if (obj.getClass().isArray()) {
            return Arrays.stream((Object[]) obj)
                    .map(this::serializeElement)
                    .collect(Collectors.joining(", ", "[", "]"));
        }

        return serializeElement(obj);
    }

    private String serializeElement(Object element) {
        if (element == null) return "null";

        if (element instanceof HttpMethod || element instanceof ParameterizedTypeReference) return "\"" + element + "\"";

        try {
            return objectMapper.writeValueAsString(element);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize {} to JSON: {}. Using toString().",
                    element.getClass().getSimpleName(), e.getMessage());
            return "\"" + escapeStringForJson(element.toString()) + "\"";
        } catch (Exception e) {
            log.error("Unexpected error serializing {}: {}",
                    element.getClass().getSimpleName(), e.getMessage(), e);
            return "\"[Serialization Error]\"";
        }
    }

    private String escapeStringForJson(String input) {
        return Optional.ofNullable(input).map(s -> s.replace("\"", "\\\"")).orElse("");
    }

    public void logMethodEntry(ProceedingJoinPoint joinPoint, String className, String methodName, String entryArrows) {
        if (!log.isInfoEnabled()) return;

        try {
            log.info("{} {}.{}() argsJson={}", entryArrows, className, methodName, toJson(joinPoint.getArgs()));
        } catch (Exception e) {
            log.warn("{} {}.{}() - Failed to serialize args: {}", entryArrows, className, methodName, e.getMessage());
            log.info("{} {}.{}() args=[Serialization Failed]", entryArrows, className, methodName);
        }
    }

    public void logMethodExit(Object result, String className, String methodName, long executionTime, String exitArrows) {
        if (!log.isInfoEnabled()) return;

        try {
            log.info("{} {}.{}() time={}ms resultJson={}",
                    exitArrows, className, methodName, executionTime, toJson(result));
        } catch (Exception e) {
            log.warn("{} {}.{}() time={}ms - Failed to serialize result: {}",
                    exitArrows, className, methodName, executionTime, e.getMessage());
            log.info("{} {}.{}() time={}ms result=[Serialization Failed]",
                    exitArrows, className, methodName, executionTime);
        }
    }

    public void logMethodException(Throwable e, String className, String methodName, long executionTime, String errorArrows, Object[] args) {
        if (!log.isDebugEnabled()) return;

        try {
            log.debug("{} {}.{}() Exception Type: {}, Message: {} time={}ms with argsJson={}",
                    errorArrows, className, methodName, e.getClass().getSimpleName(),
                    e.getMessage(), executionTime, toJson(args));
        } catch (Exception logEx) {
            log.debug("{} {}.{}() Exception Type: {}, Message: {} time={}ms with args=[Serialization Failed]",
                    errorArrows, className, methodName, e.getClass().getSimpleName(),
                    e.getMessage(), executionTime);
        }
    }

}