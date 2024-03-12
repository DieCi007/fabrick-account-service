package it.fabrick.account.feature.fabrick;

import it.fabrick.account.client.RestClientService;
import it.fabrick.account.exception.ThirdPartyException;
import it.fabrick.account.feature.fabrick.contract.FabrickBaseResponse;
import it.fabrick.account.feature.fabrick.contract.FabrickErrorResponse;
import it.fabrick.account.feature.fabrick.contract.FabrickGetBalanceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.function.Supplier;

import static it.fabrick.account.exception.GlobalErrorHandler.UNKNOWN_ERROR;
import static it.fabrick.account.exception.ThirdPartyException.ExceptionSource.FABRICK;

@Component
public class FabrickClient {
    private final RestClientService restClient;
    private final String fabrickServerBaseUrl;
    private final String fabrickServerApiKey;
    private final String fabrickServerAuthSchema;
    private final String fabrickAccountId;
    private final FabrickErrorResponse defaultErrorResponse = FabrickErrorResponse.builder()
            .code(UNKNOWN_ERROR)
            .description(UNKNOWN_ERROR).build();

    @Autowired
    public FabrickClient(RestClientService restClient,
                         @Value("${it.fabrick.server.baseUrl}") String fabrickServerBaseUrl,
                         @Value("${it.fabrick.server.apiKey}") String fabrickServerApiKey,
                         @Value("${it.fabrick.server.authSchema}") String fabrickServerAuthSchema,
                         @Value("${it.fabrick.account.id}") String fabrickAccountId) {
        this.restClient = restClient;
        this.fabrickServerBaseUrl = fabrickServerBaseUrl;
        this.fabrickServerApiKey = fabrickServerApiKey;
        this.fabrickServerAuthSchema = fabrickServerAuthSchema;
        this.fabrickAccountId = fabrickAccountId;
    }

    public FabrickGetBalanceResponse getAccountBalance() {
        var url = String.format("%s%s%s%s", fabrickServerBaseUrl, "/api/gbs/banking/v4.0/accounts/", fabrickAccountId, "/balance");
        return doExceptionAwareCall(() -> restClient.executeRequestWithRetry(
                url,
                HttpMethod.GET,
                new HttpEntity<>(getHeaders()),
                new ParameterizedTypeReference<FabrickBaseResponse<FabrickGetBalanceResponse>>() {
                }
        ).getBody());
    }

    private <T> T doExceptionAwareCall(Supplier<FabrickBaseResponse<T>> call) throws ThirdPartyException {
        try {
            var response = call.get();
            if (FabrickBaseResponse.ResponseStatus.KO.equals(response.getStatus())) {
                // Could it be that response status is 2xx but body status is KO?
                var displayError = response.getError().stream().findFirst().orElse(defaultErrorResponse);
                throw new ThirdPartyException(displayError.getDescription(), FABRICK, displayError.getCode(), HttpStatus.BAD_GATEWAY);
            }
            return response.getPayload();
        } catch (HttpStatusCodeException e) {
            var response = FabrickBaseResponse.fromResponseString(e.getResponseBodyAsString());

            // we go ahead and display only the first error message to end user
            var displayError = response.getErrors().stream().findFirst().orElse(defaultErrorResponse);
            throw new ThirdPartyException(displayError.getDescription(), FABRICK, displayError.getCode(), e.getStatusCode());
        }
    }

    private HttpHeaders getHeaders() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", fabrickServerApiKey);
        headers.set("auth-schema", fabrickServerAuthSchema);
        return headers;
    }
}
