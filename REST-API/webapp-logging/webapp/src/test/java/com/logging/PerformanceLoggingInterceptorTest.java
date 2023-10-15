package com.logging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.mockito.Mockito;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PerformanceLoggingInterceptorTest {

	private PerformanceLoggingInterceptor interceptor;
	private ExchangeFunction exchangeFunction;

	@BeforeEach
	void setUp() {
		exchangeFunction = Mockito.mock(ExchangeFunction.class);
		interceptor = new PerformanceLoggingInterceptor();
	}

	@Test
	void shouldLogSuccessfulExecutionTime() {
		long startTime = System.currentTimeMillis();
		ClientResponse clientResponse = ClientResponse.create(HttpStatusCode.valueOf(200)).build();
		ClientRequest clientRequest = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com")).build();

		Mockito.when(exchangeFunction.exchange(Mockito.any(ClientRequest.class))).thenReturn(Mono.just(clientResponse));

		Mono<ClientResponse> result = interceptor.filter(clientRequest, exchangeFunction);
		result.block();

		ArgumentCaptor<String> logCaptor = ArgumentCaptor.forClass(String.class);
		//Mockito.verify(interceptor.getLogger()).info(logCaptor.capture());

		long executionTime = System.currentTimeMillis() - startTime;

		// Verify that the logger was called with the expected log message
		String loggedMessage = logCaptor.getValue();
		assertEquals("WebClient call to http://example.com took " + executionTime + "ms", loggedMessage);
	}

	@Test
	void shouldLogErrorExecutionTime() {
		long startTime = System.currentTimeMillis();
		ClientRequest clientRequest = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com")).build();
		Exception error = new RuntimeException("Simulated error");
		Mockito.when(exchangeFunction.exchange(Mockito.any(ClientRequest.class))).thenReturn(Mono.error(error));

		Mono<ClientResponse> result = interceptor.filter(clientRequest, exchangeFunction);

		// This will throw an exception, which we will catch and verify the log message
		try {
			result.block();
		} catch (Exception ignored) {
		}

		ArgumentCaptor<String> logCaptor = ArgumentCaptor.forClass(String.class);
		//Mockito.verify(interceptor.getLogger()).error(logCaptor.capture(), Mockito.any(Throwable.class));

		long executionTime = System.currentTimeMillis() - startTime;

		// Verify that the logger was called with the expected log message
		String loggedMessage = logCaptor.getValue();
		assertEquals("WebClient call to http://example.com failed after " + executionTime + "ms", loggedMessage);
	}
}
