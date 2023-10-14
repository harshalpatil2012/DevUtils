package com.logging;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "your-custom-properties-if-any")
public class PerformanceLoggingInterceptorIntegrationTest {

	@Autowired
	private WebClient.Builder webClientBuilder;

	@Test
	void shouldLogSuccessfulExecutionTime() {
		WebClient webClient = webClientBuilder.baseUrl("http://example.com").build();

		long startTime = System.currentTimeMillis();

		Mono<String> response = webClient.get().uri("/sample").retrieve().bodyToMono(String.class);
		assertNotNull(response.block());

		long executionTime = System.currentTimeMillis() - startTime;

		ArgumentCaptor<String> logCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(interceptor.getLogger()).info(logCaptor.capture());

		// Verify that the logger was called with the expected log message
		String loggedMessage = logCaptor.getValue();
		assertTrue(loggedMessage.contains("WebClient call to http://example.com took " + executionTime + "ms"));
	}

	@Test
	void shouldLogErrorExecutionTime() {
		WebClient webClient = webClientBuilder.baseUrl("http://example.com").build();

		long startTime = System.currentTimeMillis();

		// This example assumes an error response with a 404 status code
		assertThrows(WebClientResponseException.class, () -> {
			webClient.get().uri("/non-existent").retrieve().bodyToMono(String.class).block();
		});

		long executionTime = System.currentTimeMillis() - startTime;

		ArgumentCaptor<String> logCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(interceptor.getLogger()).error(logCaptor.capture(), Mockito.any(Throwable.class));

		// Verify that the logger was called with the expected log message
		String loggedMessage = logCaptor.getValue();
		assertTrue(loggedMessage.contains("WebClient call to http://example.com failed after " + executionTime + "ms"));
	}
}