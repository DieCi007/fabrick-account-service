package it.fabrick.account.feature.account.contract;

import it.fabrick.account.feature.fabrick.contract.FabrickGetBalanceResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GetBalanceResponse {
    private LocalDate date;
    private Double balance;
    private Double availableBalance;
    private String currency;

    /**
     * Even though entities may be similar, I keep them seperated for future-proofing
     */
    public static GetBalanceResponse from(FabrickGetBalanceResponse fabrickBalance) {
        return GetBalanceResponse.builder()
                .date(fabrickBalance.getDate())
                .balance(fabrickBalance.getBalance())
                .availableBalance(fabrickBalance.getAvailableBalance())
                .currency(fabrickBalance.getCurrency())
                .build();
    }
}
