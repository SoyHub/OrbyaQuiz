package com.orbyta_admission_quiz.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;
import java.util.Optional;

public class ApiContext {

    private ApiContext() {}

    public static HttpHeaders buildDefaultHeaders() {
        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpServletRequest request = getCurrentHttpRequest();
        setHeaderIfPresent(request, Constants.API_KEY_HEADER, reqHeaders);
        setHeaderIfPresent(request, Constants.AUTH_SCHEMA_HEADER, reqHeaders);

        reqHeaders.set(Constants.X_TIME_ZONE_HEADER, Constants.EUROPE_ROME_TIME_ZONE_VALUE);

        return reqHeaders;
    }

    private static void setHeaderIfPresent(HttpServletRequest request, String headerName, HttpHeaders reqHeaders) {
        Optional.ofNullable(request.getHeader(headerName))
                .ifPresent(value -> reqHeaders.set(headerName, value));
    }

    private static HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return Objects.requireNonNull(attributes).getRequest();
    }
}