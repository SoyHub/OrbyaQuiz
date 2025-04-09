package com.orbyta_admission_quiz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.orbyta_admission_quiz.dto.account.response.Creditor;
import com.orbyta_admission_quiz.dto.account.response.CreditorAccount;
import com.orbyta_admission_quiz.dto.payments.request.MoneyTransferRequest;
import com.orbyta_admission_quiz.dto.payments.response.MoneyTransferResponse;
import com.orbyta_admission_quiz.service.PaymentsService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentsController.class)
class PaymentsControllerTest {

    private static final String BASE_URL = Constants.API_BASE_PATH + Constants.ACCOUNT_PATH;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private PaymentsService paymentsService;

    @Test
    @DisplayName("POST /accounts/{accountId}/payments/money-transfers - Success")
    void createMoneyTransfer_whenValidRequest_shouldReturnCreated() throws Exception {
        Long accountId = 12345L;

        MoneyTransferRequest request = MoneyTransferRequest.builder()
                .creditor(MoneyTransferRequest.Creditor.builder()
                        .name("Test Creditor")
                        .account(MoneyTransferRequest.CreditorAccount.builder()
                                .accountCode("IBAN123")
                                .build())
                        .build())
                .amount(new BigDecimal("250.00"))
                .currency("EUR")
                .feeType("STANDARD")
                .uri("URI123")
                .feeAccountId("123456789")
                .description("Test Payment")
                .executionDate(LocalDate.now().plusDays(1))
                .build();

        MoneyTransferResponse mockResponse = MoneyTransferResponse.builder()
                .moneyTransferId("MTID-67890")
                .status("ACCEPTED")
                .direction("OUTGOING")
                .creditor(Creditor.builder()
                        .name("Test Creditor")
                        .account(CreditorAccount.builder()
                                .accountCode("IBAN123")
                                .build())
                        .build())
                .amount(MoneyTransferResponse.Amount.builder()
                        .creditorAmount(new BigDecimal("250.00"))
                        .creditorCurrency("EUR")
                        .build())
                .build();

        when(paymentsService.createMoneyTransfer(eq(accountId), any(MoneyTransferRequest.class)))
                .thenReturn(mockResponse);
        mockMvc.perform(post(BASE_URL + "/{accountId}/payments/money-transfers", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.moneyTransferId").value("MTID-67890"))
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.creditor.name").value("Test Creditor"));
        verify(paymentsService, times(1)).createMoneyTransfer(eq(accountId), any(MoneyTransferRequest.class));
    }

    @Test
    @DisplayName("POST /accounts/{accountId}/payments/money-transfers - Bad Request (Validation Failure)")
    void createMoneyTransfer_whenInvalidRequest_shouldReturnBadRequest() throws Exception {
        MoneyTransferRequest invalidRequest = MoneyTransferRequest.builder()
                .creditor(MoneyTransferRequest.Creditor.builder()
                        .name("Test Creditor")
                        .account(MoneyTransferRequest.CreditorAccount.builder()
                                .accountCode("IBAN123")
                                .build())
                        .build())
                .feeType("STANDARD")
                .amount(new BigDecimal("100.00"))
                .currency("EUR")
                .description("Invalid Payment")
                .executionDate(LocalDate.now().plusDays(1))
                .build();

        mockMvc.perform(post(BASE_URL + "/{accountId}/payments/money-transfers", 12345L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
        verify(paymentsService, times(0)).createMoneyTransfer(any(), any());
    }
}
