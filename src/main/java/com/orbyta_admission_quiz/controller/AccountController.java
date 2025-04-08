package com.orbyta_admission_quiz.controller;


import com.orbyta_admission_quiz.dto.account.response.AccountBalanceResponse;
import com.orbyta_admission_quiz.dto.account.response.Transaction;
import com.orbyta_admission_quiz.service.AccountService;
import com.orbyta_admission_quiz.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Balance retrieved successfully", content = @Content(schema = @Schema(implementation = AccountBalanceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid account ID supplied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AccountBalanceResponse> getAccountBalance(@PathVariable(required = false) Long accountId) {
        log.info("Getting balance for account: {}", accountId);
        AccountBalanceResponse balance = accountService.getAccountBalance(accountId);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<List<Transaction>> getAccountTransactions(@PathVariable(required = false) Long accountId, @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fromAccountingDate, @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate toAccountingDate) {
        log.info("Getting transactions for account: {} from {} to {}", accountId, fromAccountingDate, toAccountingDate);

        /* Check DB - if Needed
            Optional<List<Transaction>> storedTransactions = accountService.getStoredTransactions(accountId, fromAccountingDate, toAccountingDate);
            if (storedTransactions.isPresent()) {
                log.info("Returning stored transactions");
                return ResponseEntity.ok(storedTransactions.get());
            }
        */

        List<Transaction> transactions = accountService.getAccountTransactions(accountId, fromAccountingDate, toAccountingDate);
        return ResponseEntity.ok(transactions);
    }
}