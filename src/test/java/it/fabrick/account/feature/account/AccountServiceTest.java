package it.fabrick.account.feature.account;

import it.fabrick.account.feature.account.entity.TransferRequest;
import it.fabrick.account.feature.account.repository.TransferRequestRepository;
import it.fabrick.account.feature.fabrick.FabrickClient;
import it.fabrick.account.feature.fabrick.contract.FabrickTransferRequest;
import it.fabrick.account.fixture.AccountFixtures;
import it.fabrick.account.fixture.FabrickFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private FabrickClient fabrickClient;

    @Mock
    private TransferRequestRepository transferRequestRepository;

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
    void getAccountTransactions_shouldWork() {
        var from = LocalDate.now();
        var to = from.plusDays(1);
        var fabrickResponse = FabrickFixtures.getValidFabrickTransactionResponse().getPayload();
        when(fabrickClient.getAccountTransactions(133L, from, to)).thenReturn(fabrickResponse.getList());

        var actualResponse = accountService.getAccountTransactions(133L, from, to);
        assertEquals(actualResponse.getTransactions().size(), fabrickResponse.getList().size());
        assertEquals(actualResponse.getTransactions().get(0).getTransactionId(), fabrickResponse.getList().get(0).getTransactionId());
        assertEquals(actualResponse.getTransactions().get(0).getAmount(), fabrickResponse.getList().get(0).getAmount());
        assertEquals(actualResponse.getTransactions().get(0).getDescription(), fabrickResponse.getList().get(0).getDescription());
    }

    @Test
    void createTransfer_shouldWork() {
        var captor = ArgumentCaptor.forClass(TransferRequest.class);
        var fabrickResponse = FabrickFixtures.getValidFabrickTransferResponse().getPayload();
        var request = AccountFixtures.getValidCreateTransferRequest();

        when(transferRequestRepository.save(any())).then(returnsFirstArg());
        when(fabrickClient.createTransfer(eq(12L), eq(FabrickTransferRequest.from(request)))).thenReturn(fabrickResponse);

        var actualResponse = accountService.createTransfer(12L, request);
        assertEquals(actualResponse.getStatus(), fabrickResponse.getStatus());
        verify(transferRequestRepository, times(2)).save(captor.capture());
        assertEquals(TransferRequest.Status.RECEIVED, captor.getAllValues().get(0).getStatus());
        assertEquals(TransferRequest.Status.SENT, captor.getAllValues().get(1).getStatus());
    }
}
