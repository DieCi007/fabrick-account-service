package it.fabrick.account.feature.account;

import it.fabrick.account.feature.account.contract.GetBalanceResponse;
import it.fabrick.account.feature.fabrick.FabrickClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private final FabrickClient fabrickClient;

    @Autowired
    public AccountService(FabrickClient fabrickClient) {
        this.fabrickClient = fabrickClient;
    }

    public GetBalanceResponse getAccountBalance() {
        var fabrickBalance = fabrickClient.getAccountBalance();
        return GetBalanceResponse.from(fabrickBalance);
    }
}
