package it.fabrick.account.feature.account.contract;

import it.fabrick.account.feature.fabrick.contract.FabrickTransactionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GetTransactionsResponse {
    private List<Transaction> transactions;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class Transaction {
        private String transactionId;
        private LocalDate accountingDate;
        private LocalDate valueDate;
        private Double amount;
        private String currency;
        private String description;
    }

    public static GetTransactionsResponse from(List<FabrickTransactionResponse> fabrickTransactions) {
        return GetTransactionsResponse.builder()
                .transactions(fabrickTransactions.stream()
                        .map(t -> Transaction.builder()
                                .transactionId(t.getTransactionId())
                                .accountingDate(t.getAccountingDate())
                                .valueDate(t.getValueDate())
                                .amount(t.getAmount())
                                .currency(t.getCurrency())
                                .description(t.getDescription())
                                .build())
                        .toList())
                .build();
    }
}
