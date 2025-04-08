package com.orbyta_admission_quiz.dto.payments.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.orbyta_admission_quiz.dto.account.response.Creditor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class MoneyTransferResponse {
    private String moneyTransferId;
    private String status;
    private String direction;
    private Creditor creditor;
    private BigDecimal amount;
    private String cro;
    private String uri;
    private String trn;
    private String description;
}

