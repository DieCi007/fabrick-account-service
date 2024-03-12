package it.fabrick.account.fixture;

import it.fabrick.account.feature.fabrick.contract.FabrickBaseResponse;
import it.fabrick.account.feature.fabrick.contract.FabrickErrorResponse;
import it.fabrick.account.feature.fabrick.contract.FabrickGetBalanceResponse;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public abstract class FabrickFixtures {
    public static FabrickBaseResponse<FabrickGetBalanceResponse> getValidBalanceResponse() {
        return FabrickBaseResponse.<FabrickGetBalanceResponse>builder()
                .status(FabrickBaseResponse.ResponseStatus.OK)
                .error(Collections.emptyList())
                .payload(FabrickGetBalanceResponse.builder()
                        .balance(-1.0)
                        .availableBalance(-12.1)
                        .date(LocalDate.now())
                        .currency("USD")
                        .build())
                .build();
    }

    public static FabrickBaseResponse<Void> getBaseErrorResponse() {
        return FabrickBaseResponse.<Void>builder()
                .status(FabrickBaseResponse.ResponseStatus.KO)
                .errors(List.of(FabrickErrorResponse.builder()
                        .description("error description")
                        .code("ISC1")
                        .build()))
                .build();
    }
}
