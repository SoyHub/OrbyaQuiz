package com.orbyta_admission_quiz.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.orbyta_admission_quiz.client.fabrick.FabrickClient;
import com.orbyta_admission_quiz.dto.account.response.AccountBalanceResponse;
import com.orbyta_admission_quiz.dto.account.response.AccountTransactionsResponse;
import com.orbyta_admission_quiz.dto.account.response.Transaction;
import com.orbyta_admission_quiz.repository.TransactionRepository;
import com.orbyta_admission_quiz.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class AccountServiceImplTest {

    private AccountServiceImpl accountService;

    @Mock
    private FabrickClient fabrickClient;

    @Mock
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        accountService = new AccountServiceImpl(fabrickClient, transactionRepository);
    }

    @Test
    void getAccountBalance_Success() {
        Long accountId = 12345678L;
        AccountBalanceResponse balanceResponse = new AccountBalanceResponse(
                new BigDecimal("1000.00"),
                new BigDecimal("800.00"),
                "EUR",
                LocalDate.now()
        );
        when(fabrickClient.getAccountBalance(accountId)).thenReturn(balanceResponse);
        AccountBalanceResponse result = accountService.getAccountBalance(accountId);
        assertNotNull(result);
        assertEquals(new BigDecimal("1000.00"), result.getBalance());
        assertEquals(new BigDecimal("800.00"), result.getAvailableBalance());
        assertEquals("EUR", result.getCurrency());
        verify(fabrickClient, times(1)).getAccountBalance(accountId);
    }

    @Test
    void getAccountTransactions_Success() {
        Long accountId = 12345678L;
        LocalDate fromDate = LocalDate.of(2023, 1, 1);
        LocalDate toDate = LocalDate.of(2023, 12, 31);
        Transaction transaction1 = new Transaction();
        transaction1.setTransactionId("1234");
        transaction1.setAmount(new BigDecimal("100.00"));
        Transaction transaction2 = new Transaction();
        transaction2.setTransactionId("5678");
        transaction2.setAmount(new BigDecimal("200.00"));
        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
        AccountTransactionsResponse response = new AccountTransactionsResponse();
        response.setList(transactions);
        when(fabrickClient.getAccountTransactions(accountId, fromDate, toDate)).thenReturn(response);
        List<Transaction> result = accountService.getAccountTransactions(accountId, fromDate, toDate);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("1234", result.get(0).getTransactionId());
        assertEquals("5678", result.get(1).getTransactionId());
        verify(fabrickClient, times(1)).getAccountTransactions(accountId, fromDate, toDate);
        verify(transactionRepository, times(1)).saveAll(transactions);
    }

    @Test
    void getStoredTransactions_Success() {
        Long accountId = 12345678L;
        LocalDate fromDate = LocalDate.of(2023, 1, 1);
        LocalDate toDate = LocalDate.of(2023, 12, 31);
        Transaction transaction = new Transaction();
        transaction.setTransactionId("9999");
        transaction.setAmount(new BigDecimal("500.00"));
        List<Transaction> transactions = List.of(transaction);
        when(transactionRepository.findByAccountIdAndAccountingDateBetween(accountId, fromDate, toDate))
                .thenReturn(transactions);
        Optional<List<Transaction>> result = accountService.getStoredTransactions(accountId, fromDate, toDate);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertEquals("9999", result.get().get(0).getTransactionId());
        verify(transactionRepository, times(1))
                .findByAccountIdAndAccountingDateBetween(accountId, fromDate, toDate);
    }

    @Test
    void getStoredTransactions_Empty() {
        Long accountId = 12345678L;
        LocalDate fromDate = LocalDate.of(2023, 1, 1);
        LocalDate toDate = LocalDate.of(2023, 12, 31);
        when(transactionRepository.findByAccountIdAndAccountingDateBetween(accountId, fromDate, toDate))
                .thenReturn(List.of());
        Optional<List<Transaction>> result = accountService.getStoredTransactions(accountId, fromDate, toDate);
        assertTrue(result.isEmpty());
        verify(transactionRepository, times(1))
                .findByAccountIdAndAccountingDateBetween(accountId, fromDate, toDate);
    }
}