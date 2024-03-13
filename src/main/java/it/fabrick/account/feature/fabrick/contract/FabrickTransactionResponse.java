package it.fabrick.account.feature.fabrick.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FabrickTransactionResponse {
    private String transactionId;
    private String operationId;
    private LocalDate accountingDate;
    private LocalDate valueDate;
    private TransactionType type;
    private Double amount;
    private String currency;
    private String description;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class TransactionType {
        private String enumeration;
        private String value;
    }
}
