package com.orbyta_admission_quiz.repository;

import com.orbyta_admission_quiz.dto.account.response.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByAccountIdAndAccountingDateBetween(Long accountId, LocalDate fromDate, LocalDate toDate);
}