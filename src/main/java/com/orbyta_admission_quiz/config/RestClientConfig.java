package com.orbyta_admission_quiz.config;

import com.orbyta_admission_quiz.util.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {

    @Value("${fabrick.api.baseUrl}")
    private String baseUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .rootUri(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(Constants.X_TIME_ZONE_HEADER, Constants.EUROPE_ROME_TIME_ZONE_VALUE)
                .build();
    }
}
