package it.fabrick.account.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class RestClientService {

    private final RestTemplate restTemplate;
    private final RetryTemplate retryTemplate;

    @Autowired
    public RestClientService(RestTemplate restTemplate, RetryTemplate retryTemplate) {
        this.restTemplate = restTemplate;
        this.retryTemplate = retryTemplate;
    }

    public <T, V> ResponseEntity<V> executeRequestWithRetry(String url, HttpMethod httpMethod, HttpEntity<T> httpEntity, ParameterizedTypeReference<V> clazz) {
        return retryTemplate.execute(retryContext -> {
            if (retryContext.getLastThrowable() != null) {
                log.info("Retry count: {} with exception: {}", retryContext.getRetryCount(), retryContext.getLastThrowable().getMessage());
            }
            return restTemplate.exchange(url, httpMethod, httpEntity, clazz);
        });
    }
}
