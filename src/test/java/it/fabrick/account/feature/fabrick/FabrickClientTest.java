package it.fabrick.account.feature.fabrick;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.fabrick.account.client.RestClientService;
import it.fabrick.account.exception.ThirdPartyException;
import it.fabrick.account.feature.fabrick.contract.FabrickBaseResponse;
import it.fabrick.account.feature.fabrick.contract.FabrickGetBalanceResponse;
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
    private final String fabrickServerApiKey = "key";
    private final String fabrickServerAuthSchema = "schema";
    private final String fabrickAccountId = "123";
    private FabrickClient fabrickClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void init() {
        this.fabrickClient = new FabrickClient(restClientService, fabrickServerBaseUrl, fabrickServerApiKey,
                fabrickServerAuthSchema, fabrickAccountId);
        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    @Test
    void getAccountBalance_shouldWork() {
        var expectedResponse = getValidBalanceResponse();
        when(restClientService.executeRequestWithRetry(
                eq(fabrickServerBaseUrl + "/api/gbs/banking/v4.0/accounts/" + fabrickAccountId + "/balance"),
                eq(HttpMethod.GET),
                any(),
                eq(new ParameterizedTypeReference<FabrickBaseResponse<FabrickGetBalanceResponse>>() {
                })
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        var actualResponse = fabrickClient.getAccountBalance();
        assertEquals(actualResponse.getBalance(), expectedResponse.getPayload().getBalance());
        assertEquals(actualResponse.getAvailableBalance(), expectedResponse.getPayload().getAvailableBalance());
        assertEquals(actualResponse.getCurrency(), expectedResponse.getPayload().getCurrency());
    }

    @Test
    void getAccountBalance_shouldThrowThirdPartyException_whenStatusCodeNotOk() throws JsonProcessingException {
        when(restClientService.executeRequestWithRetry(
                eq(fabrickServerBaseUrl + "/api/gbs/banking/v4.0/accounts/" + fabrickAccountId + "/balance"),
                eq(HttpMethod.GET),
                any(),
                eq(new ParameterizedTypeReference<FabrickBaseResponse<FabrickGetBalanceResponse>>() {
                })
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "", objectMapper.writeValueAsBytes(getBaseErrorResponse()), StandardCharsets.UTF_8));

        assertThrows(ThirdPartyException.class, () -> fabrickClient.getAccountBalance());
    }

    @Test
    void getAccountBalance_shouldThrowThirdPartyException_whenPayloadStatusIsKO() {
        var response = getValidBalanceResponse().toBuilder()
                .status(FabrickBaseResponse.ResponseStatus.KO)
                .build();
        when(restClientService.executeRequestWithRetry(
                eq(fabrickServerBaseUrl + "/api/gbs/banking/v4.0/accounts/" + fabrickAccountId + "/balance"),
                eq(HttpMethod.GET),
                any(),
                eq(new ParameterizedTypeReference<FabrickBaseResponse<FabrickGetBalanceResponse>>() {
                })
        )).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        assertThrows(ThirdPartyException.class, () -> fabrickClient.getAccountBalance());
    }
}
