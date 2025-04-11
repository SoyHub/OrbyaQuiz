package com.orbyta_admission_quiz.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggingUtilsTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private LoggingUtils loggingUtils;

    private record TestObject(String name, int value) {
        @Override
        public String toString() {
            return "TestObject{name='" + name + "', value=" + value + "}";
        }
    }

    @BeforeEach
    void setUp() {
        loggingUtils.removeDepth();
    }

    @AfterEach
    void tearDown() {
        loggingUtils.removeDepth();
    }

    @Test
    void incrementDepth_shouldIncreaseDepthByOne() {
        assertEquals(0, loggingUtils.getCurrentDepth());
        loggingUtils.incrementDepth();
        assertEquals(1, loggingUtils.getCurrentDepth());
        loggingUtils.incrementDepth();
        assertEquals(2, loggingUtils.getCurrentDepth());
    }

    @Test
    void decrementDepth_shouldDecreaseDepthByOne() {
        loggingUtils.incrementDepth();
        loggingUtils.incrementDepth();
        assertEquals(2, loggingUtils.getCurrentDepth());
        int result = loggingUtils.decrementDepth();
        assertEquals(1, loggingUtils.getCurrentDepth());
        assertEquals(1, result);
    }

    @Test
    void decrementDepth_shouldNotGoNegative() {
        assertEquals(0, loggingUtils.getCurrentDepth());
        int result = loggingUtils.decrementDepth();
        assertEquals(0, loggingUtils.getCurrentDepth());
        assertEquals(0, result);
    }

    @Test
    void getCurrentDepth_shouldReturnCurrentDepth() {
        assertEquals(0, loggingUtils.getCurrentDepth());
        loggingUtils.incrementDepth();
        assertEquals(1, loggingUtils.getCurrentDepth());
    }

    @Test
    void removeDepth_shouldRemoveThreadLocal() {
        loggingUtils.incrementDepth();
        assertEquals(1, loggingUtils.getCurrentDepth());
        loggingUtils.removeDepth();
        assertEquals(0, loggingUtils.getCurrentDepth());
    }

    @Test
    void generateArrows_shouldGenerateCorrectNumberOfArrows() {
        assertEquals(0, loggingUtils.getCurrentDepth());
        assertEquals(">", loggingUtils.generateArrows(">"));
        loggingUtils.incrementDepth();
        loggingUtils.incrementDepth();
        loggingUtils.incrementDepth();
        assertEquals(3, loggingUtils.getCurrentDepth());
        assertEquals(">>>", loggingUtils.generateArrows(">"));
        assertEquals("<<<", loggingUtils.generateArrows("<"));
    }

    @Test
    void toJson_shouldHandleNull() {
        assertEquals("null", loggingUtils.toJson(null));
    }

    @Test
    void toJson_shouldHandleNormalObject() throws JsonProcessingException {
        TestObject testObject = new TestObject("test", 123);
        when(objectMapper.writeValueAsString(testObject)).thenReturn("{\"name\":\"test\",\"value\":123}");
        String result = loggingUtils.toJson(testObject);
        assertEquals("{\"name\":\"test\",\"value\":123}", result);
        verify(objectMapper).writeValueAsString(testObject);
    }

    @Test
    void toJson_shouldHandleArray() throws JsonProcessingException {
        Object[] array = new Object[]{"string", 123, null};
        when(objectMapper.writeValueAsString("string")).thenReturn("\"string\"");
        when(objectMapper.writeValueAsString(123)).thenReturn("123");
        String result = loggingUtils.toJson(array);
        assertEquals("[\"string\", 123, null]", result);
    }

    @Test
    void toJson_shouldHandleHttpMethod() {
        HttpMethod method = HttpMethod.GET;
        String result = loggingUtils.toJson(method);
        assertEquals("\"GET\"", result);
        verifyNoInteractions(objectMapper);
    }

    @Test
    void toJson_shouldHandleParameterizedTypeReference() {
        ParameterizedTypeReference<String> typeRef = new ParameterizedTypeReference<String>() {
        };
        String result = loggingUtils.toJson(typeRef);
        assertTrue(result.startsWith("\""));
        assertTrue(result.endsWith("\""));
        verifyNoInteractions(objectMapper);
    }

    @Test
    void toJson_shouldHandleArrayWithSpecialTypes() {
        Object[] array = new Object[]{HttpMethod.POST, new ParameterizedTypeReference<String>() {
        }};
        String result = loggingUtils.toJson(array);
        assertTrue(result.startsWith("[\"POST\", \""));
        assertTrue(result.endsWith("\"]"));
        verifyNoInteractions(objectMapper);
    }

    @Test
    void toJson_shouldHandleJsonProcessingException() throws JsonProcessingException {
        TestObject testObject = new TestObject("test", 123);
        when(objectMapper.writeValueAsString(testObject)).thenThrow(new JsonProcessingException("Test error") {
        });
        String result = loggingUtils.toJson(testObject);
        assertEquals("\"TestObject{name='test', value=123}\"", result);
    }

    @Test
    void toJson_shouldHandleUnexpectedException() throws JsonProcessingException {
        TestObject testObject = new TestObject("test", 123);
        when(objectMapper.writeValueAsString(testObject)).thenThrow(new RuntimeException("Unexpected error"));
        String result = loggingUtils.toJson(testObject);
        assertEquals("\"[Serialization Error]\"", result);
    }

    @Test
    void escapeStringForJson_shouldEscapeQuotes() throws JsonProcessingException {
        TestObject testObject = new TestObject("test\"with\"quotes", 123);
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Test error") {
        });
        String result = loggingUtils.toJson(testObject);
        assertTrue(result.contains("\\\"with\\\"quotes"));
    }


}