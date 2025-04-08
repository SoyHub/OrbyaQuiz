package com.orbyta_admission_quiz.controller;


import com.orbyta_admission_quiz.dto.payments.request.MoneyTransferRequest;
import com.orbyta_admission_quiz.dto.payments.response.MoneyTransferResponse;
import com.orbyta_admission_quiz.service.PaymentsService;
import com.orbyta_admission_quiz.util.Constants;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(Constants.API_BASE_PATH + Constants.ACCOUNT_PATH)
@RequiredArgsConstructor
@Tag(name = "Account Controller", description = "Controller for managing account operations")
public class PaymentsController {

    private final PaymentsService paymentsService;

    @PostMapping("/{accountId}/payments/money-transfers")
    public ResponseEntity<Object> createMoneyTransfer(@PathVariable(required = false) Long accountId, @Valid @RequestBody MoneyTransferRequest request) {
        log.info("Creating money transfer for account: {} to {}", accountId, request.getCreditor().getName());
        MoneyTransferResponse response = paymentsService.createMoneyTransfer(accountId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}