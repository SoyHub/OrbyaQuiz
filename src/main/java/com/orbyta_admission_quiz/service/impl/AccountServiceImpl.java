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
        return fabrickClient.getAccountBalance(accountId);
    }

    @Override
    public List<Transaction> getAccountTransactions(Long accountId, LocalDate fromDate, LocalDate toDate) {
        /* If needed, uncomment the following lines to fetch saved transactions from the db
            Optional<List<Transaction>> storedTransactions = getStoredTransactions(accountId, fromDate, toDate);
            if (storedTransactions.isPresent()) {
                log.debug("Successfully fetched {} transactions from DB for account: {}", storedTransactions.get().size(), accountId);
                return storedTransactions.get();
            }
        */

        AccountTransactionsResponse response = fabrickClient.getAccountTransactions(accountId, fromDate, toDate);
        List<Transaction> transactions = response.getList();
        transactions.forEach(tx -> tx.setAccountId(accountId));

        transactionRepository.saveAll(transactions);
        return transactions;
    }

    @Override
    public Optional<List<Transaction>> getStoredTransactions(Long accountId, LocalDate fromDate, LocalDate toDate) {
        List<Transaction> transactions = transactionRepository.findByAccountIdAndAccountingDateBetween(accountId, fromDate, toDate);
        return transactions.isEmpty() ? Optional.empty() : Optional.of(transactions);
    }
}
