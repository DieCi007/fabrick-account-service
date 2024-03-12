package it.fabrick.account.feature.account;

import it.fabrick.account.feature.account.contract.GetBalanceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class AccountController {
    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("v1/account/balance")
    @ResponseStatus(HttpStatus.OK)
    public GetBalanceResponse getAccountBalance() {
        return accountService.getAccountBalance();
    }
}
