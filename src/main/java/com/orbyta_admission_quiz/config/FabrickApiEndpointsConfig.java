package com.orbyta_admission_quiz.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "fabrick.api.endpoints")
public class FabrickApiEndpointsConfig {
    private String accountBalanceEndpoint;
    private String accountTransactionsEndpoint;
    private String moneyTransferEndpoint;
}
