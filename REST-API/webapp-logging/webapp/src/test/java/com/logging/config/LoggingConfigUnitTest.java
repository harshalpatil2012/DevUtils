package com.logging.config;
import com.logging.LoggingInterceptor;
import com.logging.RequestResponseLoggingFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.filter.OncePerRequestFilter;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class LoggingConfigUnitTest {

	@MockBean
	private RequestResponseLoggingFilter requestResponseLoggingFilter;

	@MockBean
	private LoggingInterceptor loggingInterceptor;

	@Test
	void contextLoads() {
		// This test just verifies that the Spring context loads without errors.
		// It doesn't start a full application context, making it a unit test.
	}

	@Bean
	public TestRestTemplate restTemplate() {
		return new TestRestTemplate();
	}
}
