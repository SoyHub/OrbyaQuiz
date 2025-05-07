package com.orbyta_admission_quiz.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.orbyta_admission_quiz.client.fabrick.FabrickClient;
import com.orbyta_admission_quiz.dto.payments.request.MoneyTransferRequest;
import com.orbyta_admission_quiz.dto.payments.response.MoneyTransferResponse;
import com.orbyta_admission_quiz.exception.FabrickApiException;
import com.orbyta_admission_quiz.service.impl.PaymentsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PaymentsServiceImplTest {

    private PaymentsServiceImpl paymentsService;
    @Mock
    private FabrickClient fabrickClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentsService = new PaymentsServiceImpl(fabrickClient);
    }

    @Test
    void createMoneyTransfer_Success() throws FabrickApiException {
        Long accountId = 12345678L;
        MoneyTransferRequest request = MoneyTransferRequest.builder()
                .creditor(MoneyTransferRequest.Creditor.builder()
                        .name("John Doe")
                        .build())
                .build();
        MoneyTransferResponse response = new MoneyTransferResponse();
        response.setStatus("SUCCESS");
        when(fabrickClient.createMoneyTransfer(accountId, request)).thenReturn(response);
        MoneyTransferResponse result = paymentsService.createMoneyTransfer(accountId, request);
        assertNotNull(result);
        assertEquals("SUCCESS", result.getStatus());
        verify(fabrickClient, times(1)).createMoneyTransfer(accountId, request);
    }

    @Test
    void createMoneyTransfer_Failure() throws FabrickApiException {
        Long accountId = 12345678L;
        MoneyTransferRequest request = MoneyTransferRequest.builder()
                .creditor(MoneyTransferRequest.Creditor.builder()
                        .name("John Doe")
                        .build())
                .build();
        when(fabrickClient.createMoneyTransfer(accountId, request)).thenThrow(new RuntimeException("API error"));
        Exception exception = assertThrows(RuntimeException.class, () -> paymentsService.createMoneyTransfer(accountId, request));
        assertEquals("API error", exception.getMessage());
        verify(fabrickClient, times(1)).createMoneyTransfer(accountId, request);
    }
}