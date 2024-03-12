package it.fabrick.account.feature.account.contract;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateTransferRequest {
    @NotNull
    @Valid
    private Creditor creditor;
    @Size(max = 140)
    @NotBlank
    private String description;
    @NotBlank
    private String currency;
    @NotNull
    private Double amount;
    private LocalDate executionDate;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class Creditor {
        @NotBlank
        @Size(max = 70)
        private String name;
        @NotNull
        @Valid
        private Account account;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class Account {
        @NotBlank
        private String accountCode;
    }
}
