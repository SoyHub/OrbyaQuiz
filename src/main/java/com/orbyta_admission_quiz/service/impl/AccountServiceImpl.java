package com.orbyta_admission_quiz.service.impl;

import com.orbyta_admission_quiz.client.fabrick.FabrickClient;
import com.orbyta_admission_quiz.dto.account.response.AccountBalanceResponse;
import com.orbyta_admission_quiz.dto.account.response.AccountTransactionsResponse;
import com.orbyta_admission_quiz.dto.account.response.Transaction;
import com.orbyta_admission_quiz.repository.TransactionRepository;
import com.orbyta_admission_quiz.service.AccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final FabrickClient fabrickClient;
    private final TransactionRepository transactionRepository;

    @Override
    public AccountBalanceResponse getAccountBalance(Long accountId) {
        log.debug("Fetching balance for account: {}", accountId);
        AccountBalanceResponse response = fabrickClient.getAccountBalance(accountId);
        log.debug("Successfully fetched balance for account: {}", accountId);
        return response;
    }

    @Override
    public List<Transaction> getAccountTransactions(Long accountId, LocalDate fromDate, LocalDate toDate) {
        log.debug("Fetching transactions for account: {} from {} to {}", accountId, fromDate, toDate);
        AccountTransactionsResponse response = fabrickClient.getAccountTransactions(accountId, fromDate, toDate);
        List<Transaction> transactions = response.getList();
        transactions.forEach(tx -> tx.setAccountId(accountId));
        transactionRepository.saveAll(transactions);
        log.debug("Successfully fetched {} transactions for account: {}", transactions.size(), accountId);
        return transactions;
    }

    @Override
    public Optional<List<Transaction>> getStoredTransactions(Long accountId, LocalDate fromDate, LocalDate toDate) {
        log.debug("Fetching stored transactions for account: {} from {} to {}", accountId, fromDate, toDate);
        List<Transaction> transactions = transactionRepository.findByAccountIdAndAccountingDateBetween(accountId, fromDate, toDate);
        return transactions.isEmpty() ? Optional.empty() : Optional.of(transactions);
    }
}
