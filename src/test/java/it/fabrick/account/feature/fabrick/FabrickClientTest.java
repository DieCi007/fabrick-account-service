package it.fabrick.account.feature.fabrick;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.fabrick.account.client.RestClientService;
import it.fabrick.account.exception.ThirdPartyException;
import it.fabrick.account.feature.fabrick.contract.FabrickBaseResponse;
import it.fabrick.account.feature.fabrick.contract.FabrickGetBalanceResponse;
import it.fabrick.account.feature.fabrick.contract.FabrickTransferResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.StandardCharsets;

import static it.fabrick.account.fixture.FabrickFixtures.getBaseErrorResponse;
import static it.fabrick.account.fixture.FabrickFixtures.getValidBalanceResponse;
import static it.fabrick.account.fixture.FabrickFixtures.getValidFabrickTransferRequest;
import static it.fabrick.account.fixture.FabrickFixtures.getValidFabrickTransferResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FabrickClientTest {

    @Mock
    private RestClientService restClientService;
    private final String fabrickServerBaseUrl = "url";
    private FabrickClient fabrickClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void init() {
        this.fabrickClient = new FabrickClient(restClientService, fabrickServerBaseUrl, "key", "schema");
    }

    @Test
    void getAccountBalance_shouldWork() {
        var expectedResponse = getValidBalanceResponse();
        when(restClientService.executeRequestWithRetry(
                eq(fabrickServerBaseUrl + "/api/gbs/banking/v4.0/accounts/123/balance"),
                eq(HttpMethod.GET),
                any(),
                eq(new ParameterizedTypeReference<FabrickBaseResponse<FabrickGetBalanceResponse>>() {
                })
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        var actualResponse = fabrickClient.getAccountBalance(123L);
        assertEquals(actualResponse.getBalance(), expectedResponse.getPayload().getBalance());
        assertEquals(actualResponse.getAvailableBalance(), expectedResponse.getPayload().getAvailableBalance());
        assertEquals(actualResponse.getCurrency(), expectedResponse.getPayload().getCurrency());
    }

    @Test
    void getAccountBalance_shouldThrowThirdPartyException_whenStatusCodeNotOk() throws JsonProcessingException {
        when(restClientService.executeRequestWithRetry(
                eq(fabrickServerBaseUrl + "/api/gbs/banking/v4.0/accounts/111/balance"),
                eq(HttpMethod.GET),
                any(),
                eq(new ParameterizedTypeReference<FabrickBaseResponse<FabrickGetBalanceResponse>>() {
                })
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "", objectMapper.writeValueAsBytes(getBaseErrorResponse()), StandardCharsets.UTF_8));

        assertThrows(ThirdPartyException.class, () -> fabrickClient.getAccountBalance(111L));
    }

    @Test
    void getAccountBalance_shouldThrowThirdPartyException_whenPayloadStatusIsKO() {
        var response = getValidBalanceResponse().toBuilder()
                .status(FabrickBaseResponse.ResponseStatus.KO)
                .build();
        when(restClientService.executeRequestWithRetry(
                eq(fabrickServerBaseUrl + "/api/gbs/banking/v4.0/accounts/333/balance"),
                eq(HttpMethod.GET),
                any(),
                eq(new ParameterizedTypeReference<FabrickBaseResponse<FabrickGetBalanceResponse>>() {
                })
        )).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        assertThrows(ThirdPartyException.class, () -> fabrickClient.getAccountBalance(333L));
    }

    @Test
    void createTransfer_shouldWork() {
        var expectedResponse = getValidFabrickTransferResponse();
        when(restClientService.executeRequestWithRetry(
                eq(fabrickServerBaseUrl + "/api/gbs/banking/v4.0/accounts/123/payments/money-transfers"),
                eq(HttpMethod.POST),
                any(),
                eq(new ParameterizedTypeReference<FabrickBaseResponse<FabrickTransferResponse>>() {
                })
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        var actualResponse = fabrickClient.createTransfer(123L, getValidFabrickTransferRequest());
        assertEquals(actualResponse.getStatus(), expectedResponse.getPayload().getStatus());
    }

    @Test
    void createTransfer_shouldThrowThirdPartyException_whenStatusCodeNotOk() throws JsonProcessingException {
        var request = getValidFabrickTransferRequest();
        when(restClientService.executeRequestWithRetry(
                eq(fabrickServerBaseUrl + "/api/gbs/banking/v4.0/accounts/123/payments/money-transfers"),
                eq(HttpMethod.POST),
                any(),
                eq(new ParameterizedTypeReference<FabrickBaseResponse<FabrickTransferResponse>>() {
                })
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "", objectMapper.writeValueAsBytes(getBaseErrorResponse()), StandardCharsets.UTF_8));

        assertThrows(ThirdPartyException.class, () -> fabrickClient.createTransfer(123L, request));
    }
}
