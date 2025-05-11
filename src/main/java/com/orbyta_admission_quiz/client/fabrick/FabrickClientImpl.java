package com.orbyta_admission_quiz.client.fabrick;

import com.orbyta_admission_quiz.config.FabrickApiEndpointsConfig;
import com.orbyta_admission_quiz.dto.FabrickResponse;
import com.orbyta_admission_quiz.dto.account.response.AccountBalanceResponse;
import com.orbyta_admission_quiz.dto.account.response.AccountTransactionsResponse;
import com.orbyta_admission_quiz.dto.payments.request.MoneyTransferRequest;
import com.orbyta_admission_quiz.dto.payments.response.MoneyTransferResponse;
import com.orbyta_admission_quiz.exception.FabrickApiException;
import com.orbyta_admission_quiz.util.ApiContext;
import com.orbyta_admission_quiz.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class FabrickClientImpl implements FabrickClient {

    private final RestTemplate restTemplate;
    private final FabrickApiEndpointsConfig endpointsConfig;

    private <T> T execute(String url, HttpMethod method, Object body, Map<String, Object> uriVariables, ParameterizedTypeReference<FabrickResponse<T>> typeReference) throws FabrickApiException {
        try {
            ResponseEntity<FabrickResponse<T>> response = restTemplate.exchange(url, method, new HttpEntity<>(body, ApiContext.buildDefaultHeaders()), typeReference, uriVariables);
            return Objects.requireNonNull(response.getBody()).payload();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new FabrickApiException(e.getResponseBodyAsString(), e.getMessage(), e.getStatusCode().value());
        }
    }

    @Override
    public AccountBalanceResponse getAccountBalance(Long accountId) throws FabrickApiException {
        Map<String, Object> pathVars = Map.of(Constants.ACCOUNT_ID_FIELD_KEY, accountId);
        return execute(endpointsConfig.getAccountBalanceEndpoint(), HttpMethod.GET, null, pathVars, new ParameterizedTypeReference<>() {});
    }

    @Override
    public AccountTransactionsResponse getAccountTransactions(Long accountId, LocalDate fromDate, LocalDate toDate) throws FabrickApiException {
        Map<String, Object> pathVars = Map.of(Constants.ACCOUNT_ID_FIELD_KEY, accountId);

        String url = UriComponentsBuilder
                .fromUriString(endpointsConfig.getAccountTransactionsEndpoint())
                .queryParam(Constants.FROM_ACCOUNTING_DATE_FIELD_KEY, fromDate)
                .queryParam(Constants.TO_ACCOUNTING_DATE_FIELD_KEY, toDate)
                .buildAndExpand(pathVars)
                .toUriString();

        return execute(url, HttpMethod.GET, null, Collections.emptyMap(), new ParameterizedTypeReference<>() {});
    }

    @Override
    public MoneyTransferResponse createMoneyTransfer(Long accountId, MoneyTransferRequest request) throws FabrickApiException {
        Map<String, Object> pathVars = Map.of(Constants.ACCOUNT_ID_FIELD_KEY, accountId);
        return execute(endpointsConfig.getMoneyTransferEndpoint(), HttpMethod.POST, request, pathVars, new ParameterizedTypeReference<>() {});
    }
}
