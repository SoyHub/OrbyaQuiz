package com.orbyta_admission_quiz.dto.account.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountBalanceResponse {
    private BigDecimal balance;
    private BigDecimal availableBalance;
    private String currency;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
}