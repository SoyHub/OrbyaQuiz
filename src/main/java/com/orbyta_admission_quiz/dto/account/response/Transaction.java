package com.orbyta_admission_quiz.dto.account.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    private String transactionId;
    private String operationId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate accountingDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate valueDate;

    private BigDecimal amount;
    private String currency;
    private String description;
    private Long accountId;
}