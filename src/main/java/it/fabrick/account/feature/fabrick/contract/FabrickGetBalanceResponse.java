package it.fabrick.account.feature.fabrick.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class FabrickGetBalanceResponse {
    private LocalDate date;
    private Double balance;
    private Double availableBalance;
    private String currency; // TODO: map to enum if available values documentation found
}
