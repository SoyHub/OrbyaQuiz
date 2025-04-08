package com.orbyta_admission_quiz.dto.payments.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MoneyTransferRequest {

    @NotNull(message = "Creditor is required")
    private Creditor creditor;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate executionDate;

    @NotBlank(message = "URI is required")
    private String uri;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    private boolean isUrgent;
    private boolean isInstant;

    @NotBlank(message = "Fee type is required")
    private String feeType;

    @NotBlank(message = "Fee account ID is required")
    private String feeAccountId;

    private TaxRelief taxRelief;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Creditor {
        @JsonProperty("name")
        @NotBlank(message = "Creditor name is required")
        private String name;

        @JsonProperty("account")
        @NotNull(message = "Creditor account is required")
        private CreditorAccount account;

        @JsonProperty("address")
        private Address address;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreditorAccount {
        @JsonProperty("accountCode")
        @NotBlank(message = "Account code is required")
        private String accountCode;

        @JsonProperty("bicCode")
        private String bicCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        @JsonProperty("address")
        private String address;
        @JsonProperty("city")
        private String city;
        @JsonProperty("countryCode")
        private String countryCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaxRelief {
        @JsonProperty("taxReliefId")
        private String taxReliefId;
        @JsonProperty("isCondoUpgrade")
        private boolean isCondoUpgrade;
        @JsonProperty("creditorFiscalCode")
        private String creditorFiscalCode;
        @JsonProperty("beneficiaryType")
        private String beneficiaryType;

        @JsonProperty("naturalPersonBeneficiary")
        private NaturalPersonBeneficiary naturalPersonBeneficiary;
        @JsonProperty("legalPersonBeneficiary")
        private LegalPersonBeneficiary legalPersonBeneficiary;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NaturalPersonBeneficiary {
        @JsonProperty("fiscalCode1")
        private String fiscalCode1;
        @JsonProperty("fiscalCode2")
        private String fiscalCode2;
        @JsonProperty("fiscalCode3")
        private String fiscalCode3;
        @JsonProperty("fiscalCode4")
        private String fiscalCode4;
        @JsonProperty("fiscalCode5")
        private String fiscalCode5;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LegalPersonBeneficiary {
        @JsonProperty("fiscalCode")
        private String fiscalCode;
        @JsonProperty("legalRepresentativeFiscalCode")
        private String legalRepresentativeFiscalCode;
    }
}
