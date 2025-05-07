package com.orbyta_admission_quiz.service;

import com.orbyta_admission_quiz.dto.account.response.AccountBalanceResponse;
import com.orbyta_admission_quiz.dto.account.response.Transaction;
import com.orbyta_admission_quiz.exception.FabrickApiException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface AccountService {
    AccountBalanceResponse getAccountBalance(Long accountId) throws FabrickApiException;

    List<Transaction> getAccountTransactions(Long accountId, LocalDate fromDate, LocalDate toDate) throws FabrickApiException;

    Optional<List<Transaction>> getStoredTransactions(Long accountId, LocalDate fromDate, LocalDate toDate);
}
