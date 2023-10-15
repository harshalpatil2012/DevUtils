package com.logging;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = WebClientConfigTest.TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class WebClientConfigTest {

	@Autowired
	private WebClient.Builder webClientBuilder;

	@MockBean
	private ExchangeFilterFunction performanceLoggingFilter;

	@Configuration
	public static class TestConfig {

		@Bean
		public ExchangeFilterFunction performanceLoggingFilter() {
			return (request, exchangeFunction) -> {
				// Mocked behavior for the performanceLoggingFilter
				return Mono.empty();
			};
		}

		@Bean
		public WebClient.Builder webClientBuilder() {
			return WebClient.builder().filter(performanceLoggingFilter());
		}
	}

	@Test
	public void testWebClientBuilder() {
		assertNotNull(webClientBuilder);

		// Verify that the performanceLoggingFilter is invoked with the correct parameters
		ArgumentCaptor<ExchangeFilterFunction> captor = ArgumentCaptor.forClass(ExchangeFilterFunction.class);
		verify(performanceLoggingFilter).filter((ClientRequest) captor.capture(), Mockito.any());
		ExchangeFilterFunction actualFilter = captor.getValue();
		assertEquals(performanceLoggingFilter, actualFilter);
	}
}
