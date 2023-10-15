package com.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;


public class PerformanceLoggingInterceptor implements ExchangeFilterFunction {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceLoggingInterceptor.class);

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        long startTime = System.currentTimeMillis();
        String url = request.url().toString();


        return next.exchange(request).flatMap(clientResponse -> {
            long executionTime = System.currentTimeMillis() - startTime;
            logRequestAndResponse(request, clientResponse, executionTime);
            return Mono.just(clientResponse);
        }).doOnError(throwable -> {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("WebClient call to {} failed after {}ms", request.url(), executionTime, throwable);
        });

    }


    private void logRequestAndResponse(ClientRequest request, ClientResponse response, long executionTime) {
        HttpMethod method = request.method();
        HttpHeaders requestHeaders = request.headers();
        MediaType responseContentType = response.headers().contentType().orElse(MediaType.APPLICATION_OCTET_STREAM);
        HttpHeaders headers = response.headers().asHttpHeaders();


        logger.info("WebClient call to {} took {}ms", request.url(), executionTime);
        logger.info("Request: Method={}, URL={}, Headers={}", method, request.url(), requestHeaders);
        logger.info("Response: Status={}, Content-Type={}, Headers={}", response.statusCode(), responseContentType, headers);
    }
}
