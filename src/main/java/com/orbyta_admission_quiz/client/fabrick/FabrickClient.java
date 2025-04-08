package com.orbyta_admission_quiz.client.fabrick;

import com.orbyta_admission_quiz.dto.account.response.AccountBalanceResponse;
import com.orbyta_admission_quiz.dto.account.response.AccountTransactionsResponse;
import com.orbyta_admission_quiz.dto.payments.request.MoneyTransferRequest;
import com.orbyta_admission_quiz.dto.payments.response.MoneyTransferResponse;

import java.time.LocalDate;

public interface FabrickClient {
    AccountBalanceResponse getAccountBalance(Long accountId);

    AccountTransactionsResponse getAccountTransactions(Long accountId, LocalDate fromDate, LocalDate toDate);

    MoneyTransferResponse createMoneyTransfer(Long accountId, MoneyTransferRequest request);
}