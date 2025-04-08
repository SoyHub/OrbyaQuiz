package com.orbyta_admission_quiz.service.impl;

import com.orbyta_admission_quiz.client.fabrick.FabrickClient;
import com.orbyta_admission_quiz.dto.payments.request.MoneyTransferRequest;
import com.orbyta_admission_quiz.dto.payments.response.MoneyTransferResponse;
import com.orbyta_admission_quiz.service.PaymentsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentsServiceImpl implements PaymentsService {

    private final FabrickClient fabrickClient;

    @Override
    public MoneyTransferResponse createMoneyTransfer(Long accountId, MoneyTransferRequest request) {
        log.debug("Creating money transfer for account: {} to: {}", accountId, request.getCreditor().getName());
        MoneyTransferResponse response = fabrickClient.createMoneyTransfer(accountId, request);
        log.debug("Money transfer API response status: {}", response.getStatus());
        return response;
    }

}
