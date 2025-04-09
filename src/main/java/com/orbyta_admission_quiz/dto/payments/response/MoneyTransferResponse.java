package com.orbyta_admission_quiz.dto.payments.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.orbyta_admission_quiz.dto.account.response.Creditor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class MoneyTransferResponse {
    @JsonProperty("moneyTransferId")
    private String moneyTransferId;
    @JsonProperty("status")
    private String status;
    @JsonProperty("direction")
    private String direction;
    @JsonProperty("creditor")
    private Creditor creditor;
    @JsonProperty("debtor")
    private Debtor debtor;
    @JsonProperty("cro")
    private String cro;
    @JsonProperty("uri")
    private String uri;
    @JsonProperty("trn")
    private String trn;
    @JsonProperty("description")
    private String description;
    @JsonProperty("createdDatetime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private LocalDateTime createdDatetime;
    @JsonProperty("accountedDatetime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private LocalDateTime accountedDatetime;
    @JsonProperty("debtorValueDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate debtorValueDate;
    @JsonProperty("creditorValueDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate creditorValueDate;
    @JsonProperty("amount")
    private Amount amount;
    @JsonProperty("isUrgent")
    private boolean isUrgent;
    @JsonProperty("isInstant")
    private boolean isInstant;
    @JsonProperty("feeType")
    private String feeType;
    @JsonProperty("feeAccountId")
    private String feeAccountId;
    @JsonProperty("fees")
    private List<Fee> fees;
    @JsonProperty("hasTaxRelief")
    private boolean hasTaxRelief;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Debtor {
        @JsonProperty("name")
        private String name;
        @JsonProperty("account")
        private Account account;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Account {
            @JsonProperty("accountCode")
            private String accountCode;
            @JsonProperty("bicCode")
            private String bicCode;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Amount {
        @JsonProperty("debtorAmount")
        private BigDecimal debtorAmount;
        @JsonProperty("debtorCurrency")
        private String debtorCurrency;
        @JsonProperty("creditorAmount")
        private BigDecimal creditorAmount;
        @JsonProperty("creditorCurrency")
        private String creditorCurrency;
        @JsonProperty("creditorCurrencyDate")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate creditorCurrencyDate;
        @JsonProperty("exchangeRate")
        private BigDecimal exchangeRate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Fee {
        @JsonProperty("feeCode")
        private String feeCode;
        @JsonProperty("description")
        private String description;
        @JsonProperty("amount")
        private BigDecimal amount;
        @JsonProperty("currency")
        private String currency;
    }
}

