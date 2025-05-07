package com.orbyta_admission_quiz.client.fabrick;

import com.orbyta_admission_quiz.dto.account.response.AccountBalanceResponse;
import com.orbyta_admission_quiz.dto.account.response.AccountTransactionsResponse;
import com.orbyta_admission_quiz.dto.payments.request.MoneyTransferRequest;
import com.orbyta_admission_quiz.dto.payments.response.MoneyTransferResponse;
import com.orbyta_admission_quiz.exception.FabrickApiException;

import java.time.LocalDate;

public interface FabrickClient {
    AccountBalanceResponse getAccountBalance(Long accountId) throws FabrickApiException;

    AccountTransactionsResponse getAccountTransactions(Long accountId, LocalDate fromDate, LocalDate toDate) throws FabrickApiException;

    MoneyTransferResponse createMoneyTransfer(Long accountId, MoneyTransferRequest request) throws FabrickApiException;
}