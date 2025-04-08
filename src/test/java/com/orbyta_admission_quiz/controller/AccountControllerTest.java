package com.orbyta_admission_quiz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orbyta_admission_quiz.dto.account.response.AccountBalanceResponse;
import com.orbyta_admission_quiz.dto.account.response.Transaction;
import com.orbyta_admission_quiz.service.AccountService;
import com.orbyta_admission_quiz.util.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AccountService accountService;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = Constants.API_BASE_PATH + Constants.ACCOUNT_PATH;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Test
    @DisplayName("GET /accounts/{accountId}/balance - Success")
    void getAccountBalance_whenAccountIdProvided_shouldReturnBalance() throws Exception {
        Long accountId = 12345L;
        AccountBalanceResponse mockResponse = new AccountBalanceResponse();
        mockResponse.setBalance(new BigDecimal("1500.75"));
        mockResponse.setCurrency("EUR");
        mockResponse.setDate(LocalDate.now());
        when(accountService.getAccountBalance(accountId)).thenReturn(mockResponse);
        mockMvc.perform(get(BASE_URL + "/{accountId}/balance", accountId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.balance").value(1500.75))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.date").value(LocalDate.now().toString()));
        verify(accountService, times(1)).getAccountBalance(accountId);
    }

    @Test
    @DisplayName("GET /accounts/{accountId}/transactions - Success")
    void getAccountTransactions_whenValidRequest_shouldReturnTransactions() throws Exception {
        Long accountId = 98765L;
        LocalDate fromDate = LocalDate.of(2024, 1, 1);
        LocalDate toDate = LocalDate.of(2024, 1, 31);
        String fromDateStr = fromDate.format(DATE_FORMATTER);
        String toDateStr = toDate.format(DATE_FORMATTER);
        List<Transaction> mockTransactions = Arrays.asList(
                createTransaction("T1", fromDate.plusDays(5), new BigDecimal("100.00"), "Salary"),
                createTransaction("T2", fromDate.plusDays(10), new BigDecimal("25.50"), "Groceries")
        );
        when(accountService.getAccountTransactions(accountId, fromDate, toDate))
                .thenReturn(mockTransactions);
        mockMvc.perform(get(BASE_URL + "/{accountId}/transactions", accountId)
                        .param("fromAccountingDate", fromDateStr)
                        .param("toAccountingDate", toDateStr)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].transactionId").value("T1"))
                .andExpect(jsonPath("$[0].amount").value(100.00))
                .andExpect(jsonPath("$[1].transactionId").value("T2"))
                .andExpect(jsonPath("$[1].amount").value(25.50));
        verify(accountService, times(1)).getAccountTransactions(accountId, fromDate, toDate);
    }

    @Test
    @DisplayName("GET /accounts/{accountId}/transactions - Bad Request (Missing Date)")
    void getAccountTransactions_whenDateMissing_shouldReturnBadRequest() throws Exception {
        Long accountId = 98765L;
        LocalDate toDate = LocalDate.of(2024, 1, 31);
        String toDateStr = toDate.format(DATE_FORMATTER);
        mockMvc.perform(get(BASE_URL + "/{accountId}/transactions", accountId)
                        .param("toAccountingDate", toDateStr)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(accountService, times(0)).getAccountTransactions(any(), any(), any());
    }

    @Test
    @DisplayName("GET /accounts/{accountId}/transactions - Bad Request (Invalid Date Format)")
    void getAccountTransactions_whenInvalidDateFormat_shouldReturnBadRequest() throws Exception {
        Long accountId = 98765L;
        String invalidDate = "202401-01";
        String toDateStr = LocalDate.of(2024, 1, 31).format(DATE_FORMATTER);
        mockMvc.perform(get(BASE_URL + "/{accountId}/transactions", accountId)
                        .param("fromAccountingDate", invalidDate)
                        .param("toAccountingDate", toDateStr)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(accountService, times(0)).getAccountTransactions(any(), any(), any());
    }

    private Transaction createTransaction(String id, LocalDate date, BigDecimal amount, String description) {
        Transaction tx = new Transaction();
        tx.setTransactionId(id);
        tx.setAccountingDate(date);
        tx.setValueDate(date.plusDays(1));
        tx.setAmount(amount);
        tx.setCurrency("EUR");
        tx.setDescription(description);
        return tx;
    }
}
