package com.orbyta_admission_quiz.util;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingRequestInterceptor.class);
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    @Override
    @NonNull
    public ClientHttpResponse intercept(@NonNull HttpRequest request, byte @NonNull [] body, @NonNull ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, body);
        long startTime = System.currentTimeMillis();
        ClientHttpResponse response = execution.execute(request, body);
        ClientHttpResponse bufferedResponse = new CustomBufferingClientHttpResponseWrapper(response);
        logResponse(bufferedResponse, startTime);
        return bufferedResponse;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        if (log.isDebugEnabled()) {
            String reqBody = body.length > 0 ? new String(body, DEFAULT_CHARSET) : "[Empty]";
            log.debug("=========================================================");
            log.debug("FABRICK_API_REQUEST - Request Begin");
            log.debug("FABRICK_API_REQUEST - URI: {}", request.getURI());
            log.debug("FABRICK_API_REQUEST - Method: {}", request.getMethod());
            log.debug("FABRICK_API_REQUEST - Headers: {}", request.getHeaders());
            log.debug("FABRICK_API_REQUEST - Request body: {}", reqBody);
            log.debug("FABRICK_API_REQUEST - Request End");
            log.debug("========================================================");
        }
    }

    private void logResponse(ClientHttpResponse response, long startTime) throws IOException {
        if (log.isDebugEnabled()) {
            long duration = System.currentTimeMillis() - startTime;
            Charset charset = getCharset(response);
            String responseBody = StreamUtils.copyToString(response.getBody(), charset);
            String loggedResponseBody = responseBody.isEmpty() ? "[Empty]" : responseBody;

            log.debug("=======================================================");
            log.debug("FABRICK_API_RESPONSE - Response Begin");
            log.debug("FABRICK_API_RESPONSE - Status code: {}", response.getStatusCode());
            log.debug("FABRICK_API_RESPONSE - Headers: {}", response.getHeaders());
            log.debug("FABRICK_API_RESPONSE - Response body: {}", loggedResponseBody);
            log.debug("FABRICK_API_RESPONSE - Duration: {} ms", duration);
            log.debug("FABRICK_API_RESPONSE - Response End");
            log.debug("==========================================================");
        }
    }

    private Charset getCharset(ClientHttpResponse response) {
        try {
            return Optional.ofNullable(response.getHeaders().getContentType())
                    .map(MediaType::getCharset)
                    .orElse(DEFAULT_CHARSET);
        } catch (Exception e) {
            log.warn("Could not determine charset from response headers. Defaulting to {}.", DEFAULT_CHARSET, e);
            return DEFAULT_CHARSET;
        }
    }
}