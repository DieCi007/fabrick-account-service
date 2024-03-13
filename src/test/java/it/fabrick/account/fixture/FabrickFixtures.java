package it.fabrick.account.fixture;

import it.fabrick.account.feature.fabrick.contract.FabrickBaseListResponse;
import it.fabrick.account.feature.fabrick.contract.FabrickBaseResponse;
import it.fabrick.account.feature.fabrick.contract.FabrickCreditor;
import it.fabrick.account.feature.fabrick.contract.FabrickErrorResponse;
import it.fabrick.account.feature.fabrick.contract.FabrickGetBalanceResponse;
import it.fabrick.account.feature.fabrick.contract.FabrickTransactionResponse;
import it.fabrick.account.feature.fabrick.contract.FabrickTransferRequest;
import it.fabrick.account.feature.fabrick.contract.FabrickTransferResponse;

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


    public static FabrickTransferRequest getValidFabrickTransferRequest() {
        return FabrickTransferRequest.builder()
                .creditor(FabrickCreditor.builder()
                        .name("John")
                        .account(FabrickCreditor.Account.builder()
                                .accountCode("IT23A0336844430152923804660")
                                .build())
                        .build())
                .executionDate(LocalDate.of(2023, 3, 19))
                .description("description")
                .amount(444D)
                .currency("EUR")
                .build();
    }

    public static FabrickBaseResponse<FabrickTransferResponse> getValidFabrickTransferResponse() {
        return FabrickBaseResponse.<FabrickTransferResponse>builder()
                .status(FabrickBaseResponse.ResponseStatus.OK)
                .error(Collections.emptyList())
                .payload(FabrickTransferResponse.builder()
                        .status(FabrickTransferResponse.TransferStatus.BOOKED)
                        .build())
                .build();
    }

    public static FabrickBaseResponse<FabrickBaseListResponse<FabrickTransactionResponse>> getValidFabrickTransactionResponse() {
        return FabrickBaseResponse.<FabrickBaseListResponse<FabrickTransactionResponse>>builder()
                .status(FabrickBaseResponse.ResponseStatus.OK)
                .error(Collections.emptyList())
                .payload(FabrickBaseListResponse.<FabrickTransactionResponse>builder()
                        .list(List.of(FabrickTransactionResponse.builder()
                                .transactionId("123123132")
                                .operationId("00000012323")
                                .accountingDate(LocalDate.of(2023, 12, 12))
                                .valueDate(LocalDate.of(2023, 12, 12))
                                .type(FabrickTransactionResponse.TransactionType.builder()
                                        .enumeration("GBS_TRANSACTION_TYPE")
                                        .value("GBS_TRANSACTION_TYPE_0023")
                                        .build())
                                .amount(-30D)
                                .currency("EUR")
                                .description("description")
                                .build()))
                        .build())
                .build();
    }
}
