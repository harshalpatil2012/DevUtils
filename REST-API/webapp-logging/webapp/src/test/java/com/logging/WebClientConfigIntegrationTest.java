package com.logging;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebClientConfigIntegrationTest {

	@LocalServerPort
	private int port;

	@Autowired
	private WebClient.Builder webClientBuilder;

	@Test
	public void testWebClientWithPerformanceFilter() {
		WebClient webClient = webClientBuilder.baseUrl("http://localhost:" + port).build();

		assertNotNull(webClient);

		// Make an actual request using WebClient
		Mono<String> response = webClient.get().uri("/sample").retrieve().bodyToMono(String.class);
		assertNotNull(response);

		// Assert that the response is not empty
		String responseBody = response.block();
		assertNotNull(responseBody);

		// You can add more specific assertions for the response content if needed
		assertEquals("Expected Response", responseBody);
	}
}