package com.logging;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoggingInterceptorIntegrationTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void testPreHandleIntegration() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Customer-Number", "12345");
		headers.set("Type", "Type-A");
		headers.set("Correlation-Id", "corr-123");

		ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:" + port + "/example",
				String.class, headers);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}
}
