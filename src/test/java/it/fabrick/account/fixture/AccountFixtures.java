package it.fabrick.account.fixture;

import it.fabrick.account.feature.account.contract.CreateTransferRequest;
import it.fabrick.account.feature.account.contract.CreateTransferResponse;
import it.fabrick.account.feature.account.contract.GetBalanceResponse;
import it.fabrick.account.feature.account.contract.GetTransactionsResponse;
import it.fabrick.account.feature.fabrick.contract.FabrickTransferResponse;

import java.time.LocalDate;

public abstract class AccountFixtures {
    public static GetBalanceResponse getValidGetBalanceResponse() {
        return GetBalanceResponse.builder()
                .availableBalance(-112.1)
                .currency("GBP")
                .build();
    }

    public static CreateTransferResponse getValidCreateTransferResponse() {
        return CreateTransferResponse.builder()
                .status(FabrickTransferResponse.TransferStatus.CANCELLED)
                .build();
    }

    public static CreateTransferRequest getValidCreateTransferRequest() {
        return CreateTransferRequest.builder()
                .creditor(CreateTransferRequest.Creditor.builder()
                        .name("John")
                        .account(CreateTransferRequest.Account.builder()
                                .accountCode("IT23A0336844430152923804660")
                                .build())
                        .build())
                .executionDate(LocalDate.of(2023, 3, 19))
                .description("description")
                .amount(444D)
                .currency("EUR")
                .build();
    }

    public static GetTransactionsResponse getValidGetTransactionsResponse() {
        return GetTransactionsResponse.from(FabrickFixtures.getValidFabrickTransactionResponse().getPayload().getList());
    }
}
