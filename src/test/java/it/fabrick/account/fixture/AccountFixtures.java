package it.fabrick.account.fixture;

import it.fabrick.account.feature.account.contract.GetBalanceResponse;

import java.time.LocalDate;

public abstract class AccountFixtures {
    public static GetBalanceResponse getValidGetBalanceResponse() {
        return GetBalanceResponse.builder()
                .balance(-2.0)
                .availableBalance(-112.1)
                .date(LocalDate.now())
                .currency("GBP")
                .build();
    }
}
