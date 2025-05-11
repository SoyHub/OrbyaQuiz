package com.orbyta_admission_quiz.controller;


import com.orbyta_admission_quiz.dto.account.response.AccountBalanceResponse;
import com.orbyta_admission_quiz.dto.account.response.Transaction;
import com.orbyta_admission_quiz.exception.FabrickApiException;
import com.orbyta_admission_quiz.service.AccountService;
import com.orbyta_admission_quiz.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(Constants.API_BASE_PATH + Constants.ACCOUNT_PATH)
@RequiredArgsConstructor
@Tag(name = "Account Controller", description = "Controller for managing account operations")
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/{accountId}/balance")
    @Operation(summary = "Get account balance by account ID", description = "Fetches the balance for a specific account ID. If no account ID is provided, the default account ID is used.")
    public AccountBalanceResponse getAccountBalance(@PathVariable Long accountId) throws FabrickApiException {
        return accountService.getAccountBalance(accountId);
    }

    @Operation(summary = "Get account transactions by account ID and date range", description = "Fetches transactions for a specific account ID within a given date range.")
    @GetMapping("/{accountId}/transactions")
    public List<Transaction> getAccountTransactions(@PathVariable Long accountId, @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fromAccountingDate, @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate toAccountingDate) throws FabrickApiException {
        return accountService.getAccountTransactions(accountId, fromAccountingDate, toAccountingDate);
    }
}