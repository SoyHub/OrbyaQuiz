package com.orbyta_admission_quiz.controller;


import com.orbyta_admission_quiz.dto.payments.request.MoneyTransferRequest;
import com.orbyta_admission_quiz.dto.payments.response.MoneyTransferResponse;
import com.orbyta_admission_quiz.exception.FabrickApiException;
import com.orbyta_admission_quiz.service.PaymentsService;
import com.orbyta_admission_quiz.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Payments Controller", description = "Controller for managing payments operations")
public class PaymentsController {

    private final PaymentsService paymentsService;

    @Operation(summary = "Create a money transfer", description = "Creates a money transfer for the specified account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Money transfer created successfully", content = @Content(schema = @Schema(implementation = MoneyTransferResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    }) // To show how to use manual swagger annotations
    @PostMapping("/{accountId}/payments/money-transfers")
    public ResponseEntity<Object> createMoneyTransfer(@PathVariable Long accountId, @Valid @RequestBody MoneyTransferRequest request) throws FabrickApiException {
        MoneyTransferResponse response = paymentsService.createMoneyTransfer(accountId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}