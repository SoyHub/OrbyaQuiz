package com.orbyta_admission_quiz.service;

import com.orbyta_admission_quiz.dto.account.response.AccountBalanceResponse;
import com.orbyta_admission_quiz.dto.account.response.Transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface AccountService {
    AccountBalanceResponse getAccountBalance(Long accountId);

    List<Transaction> getAccountTransactions(Long accountId, LocalDate fromDate, LocalDate toDate);

    Optional<List<Transaction>> getStoredTransactions(Long accountId, LocalDate fromDate, LocalDate toDate);
}
