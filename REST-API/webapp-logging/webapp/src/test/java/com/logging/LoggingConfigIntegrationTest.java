package com.logging;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;


import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "your-custom-properties-if-any")
public class LoggingConfigIntegrationTest {

    @Autowired
    private RequestResponseLoggingFilter requestResponseLoggingFilter;

    @Autowired
    private LoggingInterceptor loggingInterceptor;

    @Test
    void contextLoads() {
        // This test verifies that the Spring context loads, including the filters and interceptors.
        assertNotNull(requestResponseLoggingFilter);
        assertNotNull(loggingInterceptor);
    }
}