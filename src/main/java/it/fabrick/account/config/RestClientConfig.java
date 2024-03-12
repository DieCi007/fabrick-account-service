package it.fabrick.account.config;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.classify.Classifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.Serial;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
@AllArgsConstructor
@NoArgsConstructor
public class RestClientConfig {

    @Value("${connection.timeout:30000}")
    private Integer connectionTimeout;
    @Value("${request.timeout:30000}")
    private Integer requestTimeout;
    @Value("${retry.backoff.period:1000}")
    private Integer retryBackoffPeriod;
    @Value("${retry.attempts:3}")
    private Integer retryAttempts;

    static class RestRetryPolicy extends ExceptionClassifierRetryPolicy {

        @Serial
        private static final long serialVersionUID = 5715304049229729332L;

        RestRetryPolicy(int attempts) {
            final SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
            simpleRetryPolicy.setMaxAttempts(attempts);

            this.setExceptionClassifier((Classifier<Throwable, RetryPolicy>) ex -> {
                if (ex instanceof HttpStatusCodeException httpStatusCodeException) {
                    var status = httpStatusCodeException.getStatusCode();
                    if (status.is4xxClientError()) {
                        log.info("Got {} response with message: {} not retried", status.value(), ex.getMessage());
                        return new NeverRetryPolicy();
                    }
                }
                return simpleRetryPolicy;
            });
        }
    }

    static class CustomRetryListener implements RetryListener {
        @Override
        public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
                                                   Throwable throwable) {
            if (context.getRetryCount() > 1) {
                log.warn("Retry attempts exhausted with error message: {}",
                        throwable != null ? throwable.getMessage() : "");
            }
        }
    }

    @Bean
    public RetryTemplate retryTemplate() {
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(retryBackoffPeriod);
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
        retryTemplate.setRetryPolicy(new RestRetryPolicy(retryAttempts));
        retryTemplate.registerListener(new CustomRetryListener());
        return retryTemplate;
    }

    @Bean
    public RestTemplate restTemplate() {
        var httpClient = HttpClients.custom()
                .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                        .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                                .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                                .build())
                        .build())
                .disableCookieManagement()
                .build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        requestFactory.setConnectionRequestTimeout(requestTimeout);
        requestFactory.setConnectTimeout(connectionTimeout);

        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(requestFactory));
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }
}
