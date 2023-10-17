import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

@Component
@Order(1)
public class SecurityHeadersFilter implements WebFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityHeadersFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        logRequestHeaders(exchange);
        
        // Set secure headers
        exchange.getResponse().getHeaders().set("X-Content-Type-Options", "nosniff");
        exchange.getResponse().getHeaders().set("X-Frame-Options", "DENY");
        exchange.getResponse().getHeaders().set("X-XSS-Protection", "1; mode=block");
        exchange.getResponse().getHeaders().set("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        exchange.getResponse().getHeaders().set("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        exchange.getResponse().getHeaders().set("Referrer-Policy", "same-origin");
        
        return chain.filter(exchange);
    }
    
    private void logRequestHeaders(ServerWebExchange exchange) {
        logger.info("User-Agent: " + exchange.getRequest().getHeaders().getFirst("User-Agent"));
        logger.info("Referer: " + exchange.getRequest().getHeaders().getFirst("Referer"));
        logger.info("Accept-Language: " + exchange.getRequest().getHeaders().getFirst("Accept-Language"));
        logger.info("X-Forwarded-For: " + exchange.getRequest().getHeaders().getFirst("X-Forwarded-For"));
    }
}




import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class SecurityHeadersFilterTest {

    private SecurityHeadersFilter filter;
    private MockFilterConfig filterConfig;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    public void setUp() {
        filter = new SecurityHeadersFilter();
        filterConfig = new MockFilterConfig();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    public void testPositiveScenario() throws Exception {
        filter.init(filterConfig);
        MockFilterChain filterChain = new MockFilterChain();
        
        filter.doFilter(request, response, filterChain);

        // Verify that the secure headers are set as expected
        Mockito.verify(response).setHeader("X-Content-Type-Options", "nosniff");
        Mockito.verify(response).setHeader("X-Frame-Options", "DENY");
        Mockito.verify(response).setHeader("X-XSS-Protection", "1; mode=block");
        Mockito.verify(response).setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        Mockito.verify(response).setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        Mockito.verify(response).setHeader("Referrer-Policy", "same-origin");

        // Verify that the filter chain continues
        Mockito.verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testNegativeScenario() throws Exception {
        filter.init(filterConfig);
        MockFilterChain filterChain = new MockFilterChain();
        
        // Simulate a scenario where the filter is supposed to block access (e.g., due to a security condition)
        // Insert your negative scenario logic here, and ensure the filter behaves as expected.

        // Verify that the filter chain is not called
        Mockito.verify(filterChain, Mockito.never()).doFilter(request, response);
    }

    @Test
    public void testCustomCacheControlHeader() throws Exception {
        filter.init(filterConfig);
        MockFilterChain filterChain = new MockFilterChain();
        
        // Simulate a scenario where you customize the Cache-Control header
        // Set up your request and response objects accordingly.

        filter.doFilter(request, response, filterChain);

        // Verify that the Cache-Control header is set as expected in this custom scenario
        Mockito.verify(response).setHeader("Cache-Control", "custom-cache-settings");
    }
}


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityHeadersFilterIntegrationTest {

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void testPositiveScenario() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/your-endpoint", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().containsKey("X-Content-Type-Options"));
        assertTrue(response.getHeaders().containsKey("X-Frame-Options"));
        assertTrue(response.getHeaders().containsKey("X-XSS-Protection"));
        assertTrue(response.getHeaders().containsKey("Strict-Transport-Security"));
        assertTrue(response.getHeaders().containsKey("Cache-Control"));
        assertTrue(response.getHeaders().containsKey("Referrer-Policy"));
    }

    @Test
    public void testNegativeScenario() {
        // Send an HTTP request to your application and verify the behavior in a negative scenario (e.g., access blocked)
        // Insert your negative scenario logic here, and ensure the filter behaves as expected.
    }

    @Test
    public void testCustomCacheControlHeader() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/your-endpoint", String.class);

        assertEquals("custom-cache-settings", response.getHeaders().getFirst("Cache-Control"));
    }
}
