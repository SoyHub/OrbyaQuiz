package com.orbyta_admission_quiz.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        if (currentDepth > 0) {
            callDepth.set(currentDepth - 1);
            return currentDepth - 1;
        } else {
            callDepth.set(0);
            return 0;
        }
    }

    public int getCurrentDepth() {
        return callDepth.get();
    }

    public void removeDepth() {
        callDepth.remove();
    }

    public String generateArrows(String arrowChar) {
        int depth = getCurrentDepth();
        int displayDepth = Math.max(1, depth);
        return IntStream.range(0, displayDepth)
                .mapToObj(i -> arrowChar)
                .collect(Collectors.joining());
    }

    public String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }

        if (obj.getClass().isArray()) {
            Object[] array = (Object[]) obj;
            return Arrays.stream(array)
                    .map(this::serializeArrayElement)
                    .collect(Collectors.joining(", ", "[", "]"));
        } else {
            return serializeObject(obj);
        }
    }

    private String serializeArrayElement(Object element) {
        if (element == null) {
            return "null";
        }
        if (element instanceof HttpMethod) {
            return "\"" + element + "\"";
        }
        if (element instanceof ParameterizedTypeReference) {
            return "\"" + element + "\"";
        }
        return serializeObject(element);
    }


    private String serializeObject(Object obj) {
        if (obj == null) {
            return "null";
        }

        if (obj instanceof ParameterizedTypeReference) {
            return "\"" + obj.toString() + "\"";
        }
        if (obj instanceof HttpMethod) {
            return "\"" + obj.toString() + "\"";
        }

        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize object {} to JSON: {}. Falling back to toString().",
                    obj.getClass().getSimpleName(), e.getMessage());
            return "\"" + escapeStringForJson(obj.toString()) + "\"";
        } catch (Exception e) {
            log.error("Unexpected error during JSON serialization of {}: {}",
                    obj.getClass().getSimpleName(), e.getMessage(), e);
            return "\"[Serialization Error]\"";
        }
    }

    private String escapeStringForJson(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"");
    }
}