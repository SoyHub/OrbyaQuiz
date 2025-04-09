package com.orbyta_admission_quiz.client;

import com.orbyta_admission_quiz.client.fabrick.FabrickClientImpl;
import com.orbyta_admission_quiz.config.FabrickApiEndpointsConfig;
import com.orbyta_admission_quiz.dto.account.response.AccountBalanceResponse;
import com.orbyta_admission_quiz.dto.account.response.AccountTransactionsResponse;
import com.orbyta_admission_quiz.dto.FabrickResponse;
import com.orbyta_admission_quiz.dto.account.response.Transaction;
import com.orbyta_admission_quiz.dto.payments.request.MoneyTransferRequest;
import com.orbyta_admission_quiz.dto.payments.response.MoneyTransferResponse;
import com.orbyta_admission_quiz.exception.FabrickApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FabrickClientImplTest {

    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private FabrickClientImpl fabrickClient;
    @Mock
    private FabrickApiEndpointsConfig endpointsConfig;

    private HttpHeaders defaultHeaders;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader("Mock-Header", "MockValue");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
    }

    @Test
    @DisplayName("getAccountBalance - Success")
    void getAccountBalance_success() {
        Long accountId = 12345L;
        Map<String, Object> pathVars = Map.of("accountId", accountId);

        AccountBalanceResponse mockPayload = new AccountBalanceResponse();
        mockPayload.setAvailableBalance(new BigDecimal("1000.50"));
        mockPayload.setCurrency("EUR");

        FabrickResponse<AccountBalanceResponse> fabrickResponse = new FabrickResponse<>("OK", null, mockPayload);

        ResponseEntity<FabrickResponse<AccountBalanceResponse>> mockResponseEntity =
                new ResponseEntity<>(fabrickResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(endpointsConfig.getAccountBalanceEndpoint()),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class),
                eq(pathVars)))
                .thenReturn(mockResponseEntity);

        AccountBalanceResponse result = fabrickClient.getAccountBalance(accountId);

        assertNotNull(result);
        assertEquals(new BigDecimal("1000.50"), result.getAvailableBalance());
        assertEquals("EUR", result.getCurrency());
    }

    @Test
    @DisplayName("getAccountTransactions - Success")
    void getAccountTransactions_success() {
        Long accountId = 67890L;
        LocalDate fromDate = LocalDate.of(2024, 1, 1);
        LocalDate toDate = LocalDate.of(2024, 1, 31);
        Map<String, Object> pathVars = Map.of("accountId", accountId);
        when(endpointsConfig.getAccountTransactionsEndpoint()).thenReturn("/api/gbs/banking/v4.0/accounts/{accountId}/transactions");

        String expectedUrl = UriComponentsBuilder
                .fromUriString(endpointsConfig.getAccountTransactionsEndpoint())
                .queryParam("fromAccountingDate", fromDate)
                .queryParam("toAccountingDate", toDate)
                .buildAndExpand(pathVars)
                .toUriString();

        AccountTransactionsResponse mockPayload = new AccountTransactionsResponse();
        mockPayload.setList(Collections.singletonList(new Transaction()));

        FabrickResponse<AccountTransactionsResponse> fabrickResponse = new FabrickResponse<>("OK", null, mockPayload);

        ResponseEntity<FabrickResponse<AccountTransactionsResponse>> mockResponseEntity =
                new ResponseEntity<>(fabrickResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class),
                eq(Collections.emptyMap())))
                .thenReturn(mockResponseEntity);

        AccountTransactionsResponse result = fabrickClient.getAccountTransactions(accountId, fromDate, toDate);

        assertNotNull(result);
        assertNotNull(result.getList());
        assertEquals(1, result.getList().size());
    }

    @Test
    @DisplayName("getAccountBalance - API Error (HttpClientErrorException)")
    void getAccountBalance_shouldThrowFabrickApiException_whenApiReturnsError() {
        Long accountId = 12345L;
        Map<String, Object> pathVars = Map.of("accountId", accountId);
        HttpStatus errorStatus = HttpStatus.NOT_FOUND;
        String errorMessage = "Account not found";

        HttpClientErrorException exception = HttpClientErrorException.create(
                errorMessage, errorStatus, errorStatus.getReasonPhrase(), defaultHeaders, null, Charset.defaultCharset());

        when(restTemplate.exchange(
                eq(endpointsConfig.getAccountBalanceEndpoint()),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class),
                eq(pathVars)))
                .thenThrow(exception);

        FabrickApiException thrown = assertThrows(FabrickApiException.class, () -> fabrickClient.getAccountBalance(accountId));

        assertEquals(errorMessage, thrown.getRawMessage());
        assertEquals(errorStatus.value(), thrown.getHttpStatus());
    }

    @Test
    @DisplayName("getAccountTransactions - API Error")
    void getAccountTransactions_shouldThrowFabrickApiException_whenApiReturnsError() {
        Long accountId = 67890L;
        LocalDate fromDate = LocalDate.of(2024, 1, 1);
        LocalDate toDate = LocalDate.of(2024, 1, 31);
        Map<String, Object> pathVars = Map.of("accountId", accountId);
        HttpStatus errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorMessage = "Internal Server Error";

        when(endpointsConfig.getAccountTransactionsEndpoint()).thenReturn("/api/gbs/banking/v4.0/accounts/{accountId}/payments/money-transfers");

        String expectedUrl = UriComponentsBuilder
                .fromUriString(endpointsConfig.getAccountTransactionsEndpoint())
                .queryParam("fromAccountingDate", fromDate)
                .queryParam("toAccountingDate", toDate)
                .buildAndExpand(pathVars)
                .toUriString();

        HttpClientErrorException exception = HttpClientErrorException.create(
                errorMessage, errorStatus, errorStatus.getReasonPhrase(), defaultHeaders, null, Charset.defaultCharset());

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class),
                eq(Collections.emptyMap())))
                .thenThrow(exception);

        FabrickApiException thrown = assertThrows(FabrickApiException.class, () -> fabrickClient.getAccountTransactions(accountId, fromDate, toDate));

        assertEquals(errorMessage, thrown.getRawMessage());
        assertEquals(errorStatus.value(), thrown.getHttpStatus());
    }

    @Test
    @DisplayName("createMoneyTransfer - Success")
    void createMoneyTransfer_success() {
        Long accountId = 55555L;
        Map<String, Object> pathVars = Map.of("accountId", accountId);
        MoneyTransferRequest requestBody = new MoneyTransferRequest();

        MoneyTransferResponse mockPayload = new MoneyTransferResponse();
        mockPayload.setMoneyTransferId("MT-123");
        mockPayload.setStatus("EXECUTED");

        FabrickResponse<MoneyTransferResponse> fabrickResponse = new FabrickResponse<>("OK", null, mockPayload);

        ResponseEntity<FabrickResponse<MoneyTransferResponse>> mockResponseEntity =
                new ResponseEntity<>(fabrickResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(endpointsConfig.getMoneyTransferEndpoint()),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class),
                eq(pathVars)))
                .thenReturn(mockResponseEntity);
        MoneyTransferResponse result = fabrickClient.createMoneyTransfer(accountId, requestBody);
        assertNotNull(result);
        assertEquals("MT-123", result.getMoneyTransferId());
        assertEquals("EXECUTED", result.getStatus());
    }

    @Test
    @DisplayName("createMoneyTransfer - API Error")
    void createMoneyTransfer_shouldThrowFabrickApiException_whenApiReturnsError() {
        Long accountId = 55555L;
        Map<String, Object> pathVars = Map.of("accountId", accountId);
        MoneyTransferRequest requestBody = new MoneyTransferRequest();
        HttpStatus errorStatus = HttpStatus.BAD_REQUEST;
        String errorMessage = "Validation Error";

        HttpClientErrorException exception = HttpClientErrorException.create(
                errorMessage, errorStatus, errorStatus.getReasonPhrase(), defaultHeaders, null, Charset.defaultCharset());

        when(restTemplate.exchange(
                eq(endpointsConfig.getMoneyTransferEndpoint()),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class),
                eq(pathVars)))
                .thenThrow(exception);

        FabrickApiException thrown = assertThrows(FabrickApiException.class, () -> fabrickClient.createMoneyTransfer(accountId, requestBody));

        assertEquals(errorMessage, thrown.getRawMessage());
        assertEquals(errorStatus.value(), thrown.getHttpStatus());
    }
}
