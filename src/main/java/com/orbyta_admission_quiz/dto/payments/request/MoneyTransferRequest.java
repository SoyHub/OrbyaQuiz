package com.orbyta_admission_quiz.dto.payments.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MoneyTransferRequest {

    @NotNull(message = "Creditor is required")
    private Creditor creditor;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate executionDate;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    private Boolean isUrgent;
    private Boolean isInstant;
    private String feeType;
    private String feeAccountId;
    private String uri;
    private TaxRelief taxRelief;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CreditorAccount {
        @JsonProperty("accountCode")
        @NotBlank(message = "Account code is required")
        private String accountCode;

        @JsonProperty("bicCode")
        private String bicCode;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Address {
        @JsonProperty("address")
        private String addr;
        @JsonProperty("city")
        private String city;
        @JsonProperty("countryCode")
        private String countryCode;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TaxRelief {
        @JsonProperty("taxReliefId")
        private String taxReliefId;
        @JsonProperty("isCondoUpgrade")
        private Boolean isCondoUpgrade;
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LegalPersonBeneficiary {
        @JsonProperty("fiscalCode")
        private String fiscalCode;
        @JsonProperty("legalRepresentativeFiscalCode")
        private String legalRepresentativeFiscalCode;
    }
}
