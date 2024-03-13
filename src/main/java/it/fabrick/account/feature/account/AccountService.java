package it.fabrick.account.feature.account;

import it.fabrick.account.feature.account.contract.CreateTransferRequest;
import it.fabrick.account.feature.account.contract.CreateTransferResponse;
import it.fabrick.account.feature.account.contract.GetBalanceResponse;
import it.fabrick.account.feature.account.contract.GetTransactionsResponse;
import it.fabrick.account.feature.account.entity.TransferRequest;
import it.fabrick.account.feature.account.repository.TransferRequestRepository;
import it.fabrick.account.feature.fabrick.FabrickClient;
import it.fabrick.account.feature.fabrick.contract.FabrickTransferRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AccountService {
    private final FabrickClient fabrickClient;
    private final TransferRequestRepository transferRequestRepository;

    @Autowired
    public AccountService(FabrickClient fabrickClient,
                          TransferRequestRepository transferRequestRepository) {
        this.fabrickClient = fabrickClient;
        this.transferRequestRepository = transferRequestRepository;
    }

    public GetBalanceResponse getAccountBalance(Long accountId) {
        var fabrickBalance = fabrickClient.getAccountBalance(accountId);
        return GetBalanceResponse.from(fabrickBalance);
    }

    public GetTransactionsResponse getAccountTransactions(Long accountId, LocalDate from, LocalDate to) {
        var fabrickTransactions = fabrickClient.getAccountTransactions(accountId, from, to);
        return GetTransactionsResponse.from(fabrickTransactions);
    }

    public CreateTransferResponse createTransfer(Long accountId, CreateTransferRequest request) {
        // Ideally I would keep track of all failed and successful requests
        var transferRequest = transferRequestRepository.save(TransferRequest.builder()
                .creditorName(request.getCreditor().getName())
                .creditorAccountCode(request.getCreditor().getAccount().getAccountCode())
                .description(request.getDescription())
                .currency(request.getCurrency())
                .amount(request.getAmount())
                .executionDate(request.getExecutionDate())
                .status(TransferRequest.Status.RECEIVED)
                .build());

        var fabrickResponse = fabrickClient.createTransfer(accountId, FabrickTransferRequest.from(request));
        transferRequestRepository.save(transferRequest.toBuilder().status(TransferRequest.Status.SENT).build());

        return CreateTransferResponse.from(fabrickResponse);
    }
}
