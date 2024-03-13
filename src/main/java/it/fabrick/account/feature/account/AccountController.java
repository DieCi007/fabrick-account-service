package it.fabrick.account.feature.account;

import it.fabrick.account.feature.account.contract.CreateTransferRequest;
import it.fabrick.account.feature.account.contract.CreateTransferResponse;
import it.fabrick.account.feature.account.contract.GetBalanceResponse;
import it.fabrick.account.feature.account.contract.GetTransactionsFilters;
import it.fabrick.account.feature.account.contract.GetTransactionsResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api")
public class AccountController {
    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("v1/account/{accountId}/balance")
    @ResponseStatus(HttpStatus.OK)
    public GetBalanceResponse getAccountBalance(@PathVariable Long accountId) {
        return accountService.getAccountBalance(accountId);
    }

    @GetMapping("v1/account/{accountId}/transactions")
    @ResponseStatus(HttpStatus.OK)
    public GetTransactionsResponse getAccountTransactions(@PathVariable Long accountId,
                                                          @Validated GetTransactionsFilters filters) {
        return accountService.getAccountTransactions(accountId, filters.getFrom(), filters.getTo());
    }

    @PostMapping("v1/account/{accountId}/money-transfer")
    @ResponseStatus(HttpStatus.OK)
    public CreateTransferResponse getAccountBalance(@Valid @RequestBody CreateTransferRequest request,
                                                    @PathVariable Long accountId) {
        return accountService.createTransfer(accountId, request);
    }
}
