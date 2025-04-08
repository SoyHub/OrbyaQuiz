package com.orbyta_admission_quiz.service;

import com.orbyta_admission_quiz.dto.payments.request.MoneyTransferRequest;
import com.orbyta_admission_quiz.dto.payments.response.MoneyTransferResponse;


public interface PaymentsService {
    MoneyTransferResponse createMoneyTransfer(Long accountId, MoneyTransferRequest request);
}
