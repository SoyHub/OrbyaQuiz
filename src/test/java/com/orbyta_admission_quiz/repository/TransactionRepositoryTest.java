package com.orbyta_admission_quiz.repository;

import com.orbyta_admission_quiz.dto.account.response.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    @DisplayName("findByAccountIdAndAccountingDateBetween - Should return transactions within date range")
    void findByAccountIdAndAccountingDateBetween_shouldReturnTransactionsWithinDateRange() {
        Long accountId = 12345L;
        LocalDate fromDate = LocalDate.of(2024, 1, 1);
        LocalDate toDate = LocalDate.of(2024, 1, 31);

        Transaction transaction1 = createTransaction("T1", accountId, fromDate.plusDays(5), new BigDecimal("100.00"), "Salary");
        Transaction transaction2 = createTransaction("T2", accountId, fromDate.plusDays(10), new BigDecimal("25.50"), "Groceries");
        Transaction transactionOutsideRange = createTransaction("T3", accountId, toDate.plusDays(1), new BigDecimal("50.00"), "Rent");

        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
        transactionRepository.save(transactionOutsideRange);

        List<Transaction> transactions = transactionRepository.findByAccountIdAndAccountingDateBetween(accountId, fromDate, toDate);

        assertThat(transactions).hasSize(2);
        assertThat(transactions).extracting(Transaction::getTransactionId).containsExactlyInAnyOrder("T1", "T2");
    }

    private Transaction createTransaction(String id, Long accountId, LocalDate accountingDate, BigDecimal amount, String description) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(id);
        transaction.setAccountId(accountId);
        transaction.setAccountingDate(accountingDate);
        transaction.setValueDate(accountingDate.plusDays(1));
        transaction.setAmount(amount);
        transaction.setCurrency("EUR");
        transaction.setDescription(description);
        return transaction;
    }
}