package it.fabrick.account.feature.account.contract;

import it.fabrick.account.validation.ValidDateRange;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ValidDateRange
public class GetTransactionsFilters {
    @NotNull
    @PastOrPresent
    private LocalDate from;

    @NotNull
    @PastOrPresent
    private LocalDate to;
}
