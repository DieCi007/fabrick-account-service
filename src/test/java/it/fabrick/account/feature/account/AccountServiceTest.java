package it.fabrick.account.feature.account;

import it.fabrick.account.feature.fabrick.FabrickClient;
import it.fabrick.account.fixture.FabrickFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        when(fabrickClient.getAccountBalance()).thenReturn(fabrickResponse);

        var actualResponse = accountService.getAccountBalance();
        assertEquals(actualResponse.getBalance(), fabrickResponse.getBalance());
        assertEquals(actualResponse.getAvailableBalance(), fabrickResponse.getAvailableBalance());
        assertEquals(actualResponse.getDate(), fabrickResponse.getDate());
    }
}
