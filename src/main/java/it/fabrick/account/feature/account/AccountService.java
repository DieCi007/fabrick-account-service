package it.fabrick.account.feature.account;

import it.fabrick.account.feature.account.contract.CreateTransferRequest;
import it.fabrick.account.feature.account.contract.CreateTransferResponse;
import it.fabrick.account.feature.account.contract.GetBalanceResponse;
import it.fabrick.account.feature.fabrick.FabrickClient;
import it.fabrick.account.feature.fabrick.contract.FabrickTransferRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private final FabrickClient fabrickClient;

    @Autowired
    public AccountService(FabrickClient fabrickClient) {
        this.fabrickClient = fabrickClient;
    }

    public GetBalanceResponse getAccountBalance(Long accountId) {
        var fabrickBalance = fabrickClient.getAccountBalance(accountId);
        return GetBalanceResponse.from(fabrickBalance);
    }

    public CreateTransferResponse createTransfer(Long accountId, CreateTransferRequest request) {
        var fabrickResponse = fabrickClient.createTransfer(accountId, FabrickTransferRequest.from(request));
        return CreateTransferResponse.from(fabrickResponse);
    }
}
