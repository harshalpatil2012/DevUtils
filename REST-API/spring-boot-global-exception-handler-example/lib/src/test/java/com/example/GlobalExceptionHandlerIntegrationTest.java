package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.Mockito.when;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@WebFluxTest
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GlobalExceptionHandlerIntegrationTest {

	@Autowired
	private WebTestClient webTestClient;

	@Container
	private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
			.withDatabaseName("test").withUsername("test").withPassword("test");

	@Test
    public void testHandleHttpClientErrorException() {
        when(itemService.getAllItems()).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found"));

        webTestClient.get().uri("/items")
                .exchange()
                .expectStatus().isNotFound();
    }

@Test
    public void testHandleHttpServerErrorException() {
        when(itemService.getAllItems()).then
