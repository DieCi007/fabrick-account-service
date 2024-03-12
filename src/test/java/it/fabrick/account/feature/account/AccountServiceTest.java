package it.fabrick.account.feature.account;

import it.fabrick.account.feature.fabrick.FabrickClient;
import it.fabrick.account.feature.fabrick.contract.FabrickTransferRequest;
import it.fabrick.account.fixture.AccountFixtures;
import it.fabrick.account.fixture.FabrickFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private FabrickClient fabrickClient;

    @InjectMocks
    private AccountService accountService;

    @Test
    void getAccountBalance_shouldWork() {
        var fabrickResponse = FabrickFixtures.getValidBalanceResponse().getPayload();
        when(fabrickClient.getAccountBalance(12L)).thenReturn(fabrickResponse);

        var actualResponse = accountService.getAccountBalance(12L);
        assertEquals(actualResponse.getAvailableBalance(), fabrickResponse.getAvailableBalance());
        assertEquals(actualResponse.getCurrency(), fabrickResponse.getCurrency());
    }

    @Test
    void createTransfer_shouldWork() {
        var fabrickResponse = FabrickFixtures.getValidFabrickTransferResponse().getPayload();
        var request = AccountFixtures.getValidCreateTransferRequest();
        when(fabrickClient.createTransfer(eq(12L), eq(FabrickTransferRequest.from(request)))).thenReturn(fabrickResponse);

        var actualResponse = accountService.createTransfer(12L, request);
        assertEquals(actualResponse.getStatus(), fabrickResponse.getStatus());
    }
}
