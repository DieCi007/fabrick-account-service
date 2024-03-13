package it.fabrick.account.feature.fabrick;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.fabrick.account.client.RestClientService;
import it.fabrick.account.exception.ThirdPartyException;
import it.fabrick.account.feature.fabrick.contract.FabrickBaseListResponse;
import it.fabrick.account.feature.fabrick.contract.FabrickBaseResponse;
import it.fabrick.account.feature.fabrick.contract.FabrickGetBalanceResponse;
import it.fabrick.account.feature.fabrick.contract.FabrickTransactionResponse;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static it.fabrick.account.fixture.FabrickFixtures.getBaseErrorResponse;
import static it.fabrick.account.fixture.FabrickFixtures.getValidBalanceResponse;
import static it.fabrick.account.fixture.FabrickFixtures.getValidFabrickTransactionResponse;
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

    @Test
    void getAccountTransactions_shouldWork() {
        var response = getValidFabrickTransactionResponse();
        var fromDate = LocalDate.of(2023, 3, 1);
        var toDate = LocalDate.of(2023, 3, 11);
        var url = UriComponentsBuilder.fromUriString(fabrickServerBaseUrl + "/api/gbs/banking/v4.0/accounts/123/transactions")
                .queryParam("fromAccountingDate", fromDate)
                .queryParam("toAccountingDate", toDate)
                .build().toString();
        when(restClientService.executeRequestWithRetry(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(new ParameterizedTypeReference<FabrickBaseResponse<FabrickBaseListResponse<FabrickTransactionResponse>>>() {
                })
        )).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
        var expectedResult = response.getPayload().getList();
        var result = fabrickClient.getAccountTransactions(123L, fromDate, toDate);
        assertEquals(expectedResult.size(), result.size());
        assertEquals(expectedResult.get(0).getTransactionId(), result.get(0).getTransactionId());
        assertEquals(expectedResult.get(0).getOperationId(), result.get(0).getOperationId());
        assertEquals(expectedResult.get(0).getDescription(), result.get(0).getDescription());
    }

    @Test
    void getAccountTransactions_shouldThrowThirdPartyException_whenStatusCodeNotOk() throws JsonProcessingException {
        var fromDate = LocalDate.of(2023, 3, 1);
        var toDate = LocalDate.of(2023, 3, 11);
        when(restClientService.executeRequestWithRetry(
                eq(UriComponentsBuilder.fromUriString(fabrickServerBaseUrl + "/api/gbs/banking/v4.0/accounts/123/transactions")
                        .queryParam("fromAccountingDate", fromDate)
                        .queryParam("toAccountingDate", toDate)
                        .build().toString()),
                eq(HttpMethod.GET),
                any(),
                eq(new ParameterizedTypeReference<FabrickBaseResponse<FabrickBaseListResponse<FabrickTransactionResponse>>>() {
                })
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "", objectMapper.writeValueAsBytes(getBaseErrorResponse()), StandardCharsets.UTF_8));

        assertThrows(ThirdPartyException.class, () -> fabrickClient.getAccountTransactions(123L, fromDate, toDate));
    }
}
