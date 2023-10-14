Step 1: Create Request and Response Logging Filter

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RequestResponseLoggingFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
            logRequest(requestWrapper);
            logResponse(responseWrapper);
        } finally {
            responseWrapper.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
            logger.info("Request URL: {}", request.getRequestURL());
            String requestBody = new String(request.getContentAsByteArray());
            logger.info("Request Body: {}", requestBody);
        }

        private void logResponse(ContentCachingResponseWrapper response) {
            logger.info("Response Headers: {}", response.getHeaderNames());
            String responseBody = new String(response.getContentAsByteArray());
            logger.info("Response Body: {}", responseBody);
    }
}
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

public class RequestResponseLoggingFilterTest {

    private RequestResponseLoggingFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @Before
    public void setup() {
        filter = new RequestResponseLoggingFilter();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = Mockito.mock(FilterChain.class);
    }

    @Test
    public void testDoFilterInternal() throws Exception {
        // Set up request data
        request.setContent("Request Content".getBytes());
        request.setRequestURI("/sample/endpoint");
        request.addHeader("Sample-Header", "TestValue");

        // Execute the filter
        filter.doFilterInternal(request, response, filterChain);

        // Add assertions for request and response data
        // For example:
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        // Assert request data
        assertEquals("Request URL: /sample/endpoint", requestWrapper.getContentAsString());

        // Assert response data
        assertEquals("Response Headers: [Sample-Header]", responseWrapper.getHeaderNames().toString());
        assertEquals("Response Body: ", responseWrapper.getContentAsString());
    }
}

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RequestResponseLoggingFilterIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testFilterIntegrationPositive() {
        webTestClient
            .get()
            .uri("/sample/endpoint")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("Expected Response Content");
        // Add assertions for positive cases
    }

    @Test
    public void testFilterIntegrationNegative() {
        webTestClient
            .get()
            .uri("/sample/non-existent-endpoint")
            .exchange()
            .expectStatus().isNotFound();
        // Add assertions for negative cases
    }
}


Step 2: Create Logging Interceptor

java

import org.springframework.web.servlet.HandlerInterceptor;
import org.apache.logging.log4j.ThreadContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String customerNumber = request.getHeader("Customer-Number");
        String type = request.getHeader("Type");
        String correlationId = request.getHeader("Correlation-Id");
        String jSessionId = request.getSession().getId();
        String sessionId = request.getRequestedSessionId();

        ThreadContext.put("CustomerNumber", customerNumber);
        ThreadContext.put("Type", type);
        ThreadContext.put("CorrelationId", correlationId);
        ThreadContext.put("JSessionId", jSessionId);
        ThreadContext.put("SessionId", sessionId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ThreadContext.clearAll();
    }
}


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class LoggingInterceptorTest {

    @Autowired
    private LoggingInterceptor interceptor;

    @MockBean
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private Object handler;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handler = new Object();
    }

    @Test
    void testPreHandlePositiveScenario() {
        request.addHeader("Customer-Number", "12345");
        request.addHeader("Type", "Type-A");
        request.addHeader("Correlation-Id", "corr-123");

        assertTrue(interceptor.preHandle(request, response, handler));
        assertEquals("12345", ThreadContext.get("CustomerNumber"));
        assertEquals("Type-A", ThreadContext.get("Type"));
        assertEquals("corr-123", ThreadContext.get("CorrelationId"));
    }

    @Test
    void testPreHandleNegativeScenario() {
        assertFalse(interceptor.preHandle(request, response, handler));
        assertNull(ThreadContext.get("CustomerNumber"));
        assertNull(ThreadContext.get("Type"));
        assertNull(ThreadContext.get("CorrelationId"));
    }

    @Test
    void testAfterCompletion() {
        request.addHeader("Customer-Number", "12345");
        request.addHeader("Type", "Type-A");
        request.addHeader("Correlation-Id", "corr-123");

        interceptor.preHandle(request, response, handler);
        interceptor.afterCompletion(request, response, handler, null);

        assertNull(ThreadContext.get("CustomerNumber"));
        assertNull(ThreadContext.get("Type"));
        assertNull(ThreadContext.get("CorrelationId"));
    }
}


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

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:" + port + "/example", String.class, headers);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}



Step 3: Configure the Filter and Interceptor in Your Spring Boot Application

java

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfig {

    @Bean
    public FilterRegistrationBean<RequestResponseLoggingFilter> requestResponseLoggingFilter() {
        FilterRegistrationBean<RequestResponseLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestResponseLoggingFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    @Bean
    public LoggingInterceptor loggingInterceptor() {
        return new LoggingInterceptor();
    }
}

import org.junit.jupiter.api.Test;
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


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.TestRestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

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


Step 4: Create Performance Logging Interceptor

import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ClientResponse;
import java.time.Duration;

public class PerformanceLoggingInterceptor implements ExchangeFilterFunction {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceLoggingInterceptor.class);

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        long startTime = System.currentTimeMillis();

        return next.exchange(request)
            .doOnSuccess(clientResponse -> {
                long executionTime = System.currentTimeMillis() - startTime;
                logger.info("WebClient call to " + request.url() + " took " + executionTime + "ms");
            })
            .doOnError(throwable -> {
                long executionTime = System.currentTimeMillis() - startTime;
                logger.error("WebClient call to " + request.url() + " failed after " + executionTime + "ms", throwable);
            });
    }
}
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.mockito.Mockito;

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
        ClientResponse clientResponse = ClientResponse.create(200).build();
        ClientRequest clientRequest = ClientRequest.create().url("http://example.com").build();

        Mockito.when(exchangeFunction.exchange(Mockito.any(ClientRequest.class))).thenReturn(Mono.just(clientResponse));

        Mono<ClientResponse> result = interceptor.filter(clientRequest, exchangeFunction);
        result.block();

        ArgumentCaptor<String> logCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(interceptor.getLogger()).info(logCaptor.capture());

        long executionTime = System.currentTimeMillis() - startTime;

        // Verify that the logger was called with the expected log message
        String loggedMessage = logCaptor.getValue();
        assertEquals("WebClient call to http://example.com took " + executionTime + "ms", loggedMessage);
    }

    @Test
    void shouldLogErrorExecutionTime() {
        long startTime = System.currentTimeMillis();
        ClientRequest clientRequest = ClientRequest.create().url("http://example.com").build();
        Exception error = new RuntimeException("Simulated error");
        Mockito.when(exchangeFunction.exchange(Mockito.any(ClientRequest.class))).thenReturn(Mono.error(error));

        Mono<ClientResponse> result = interceptor.filter(clientRequest, exchangeFunction);

        try {
            result.block();
        } catch (Exception ignored) {
        }

        ArgumentCaptor<String> logCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(interceptor.getLogger()).error(logCaptor.capture(), Mockito.any(Throwable.class));

        long executionTime = System.currentTimeMillis() - startTime;

        // Verify that the logger was called with the expected log message
        String loggedMessage = logCaptor.getValue();
        assertEquals("WebClient call to http://example.com failed after " + executionTime + "ms", loggedMessage);
    }
}


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

Step 5: Configure WebClient with the Performance Logging Interceptor

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public ExchangeFilterFunction performanceLoggingFilter() {
        return new PerformanceLoggingInterceptor();
    }

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder().filter(performanceLoggingFilter());
    }
}

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = WebClientConfigTest.TestConfig.class)
public class WebClientConfigTest {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @MockBean
    private ExchangeFilterFunction performanceLoggingFilter;

    @Configuration
    public static class TestConfig {

        @Bean
        public ExchangeFilterFunction performanceLoggingFilter() {
            return request -> {
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

        // Verify that the performanceLoggingFilter is invoked
        verify(performanceLoggingFilter).filter(Mockito.any(), Mockito.any());
    }
}

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
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

With these steps, you have implemented request and response logging using the RequestResponseLoggingFilter, context information logging using the LoggingInterceptor, and performance logging for WebClient calls using the PerformanceLoggingInterceptor. The logs will be written to the appropriate log files, and the interceptors are configured in your Spring Boot application.


===============================================
===============================================

Request and Response Logging Filter
java

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RequestResponseLoggingFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
            logRequest(requestWrapper);
            logResponse(responseWrapper);
        } finally {
            responseWrapper.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        logger.info("Request URL: " + request.getRequestURL().toString());
        String requestBody = new String(request.getContentAsByteArray());
        logger.info("Request Body: " + requestBody);
    }

    private void logResponse(ContentCachingResponseWrapper response) {
        logger.info("Response Headers: " + response.getHeaderNames());
        String responseBody = new String(response.getContentAsByteArray());
        logger.info("Response Body: " + responseBody);
    }
}
RequestResponseLoggingFilterTest
java

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

public class RequestResponseLoggingFilterTest {

    private RequestResponseLoggingFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @Before
    public void setup() {
        filter = new RequestResponseLoggingFilter();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = Mockito.mock(FilterChain.class);
    }

    @Test
    public void testDoFilterInternal() throws Exception {
        // Set up request data
        request.setContent("Request Content".getBytes());
        request.setRequestURI("/sample/endpoint");
        request.addHeader("Sample-Header", "TestValue");

        // Execute the filter
        filter.doFilterInternal(request, response, filterChain);

        // Add assertions for request and response data
        // For example:
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        // Assert request data
        assertEquals("Request URL: /sample/endpoint", requestWrapper.getContentAsString());

        // Assert response data
        assertEquals("Response Headers: [Sample-Header]", responseWrapper.getHeaderNames().toString());
        assertEquals("Response Body: ", responseWrapper.getContentAsString());
    }
}
Logging Interceptor
java

import org.springframework.web.servlet.HandlerInterceptor;
import org.apache.logging.log4j.ThreadContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String customerNumber = request.getHeader("Customer-Number");
        String type = request.getHeader("Type");
        String correlationId = request.getHeader("Correlation-Id");
        String jSessionId = request.getSession().getId();
        String sessionId = request.getRequestedSessionId();

        ThreadContext.put("CustomerNumber", customerNumber);
        ThreadContext.put("Type", type);
        ThreadContext.put("CorrelationId", correlationId);
        ThreadContext.put("JSessionId", jSessionId);
        ThreadContext.put("SessionId", sessionId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ThreadContext.clearAll();
    }
}
LoggingInterceptorTest
java

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class LoggingInterceptorTest {

    @Autowired
    private LoggingInterceptor interceptor;

    @MockBean
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private Object handler;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handler = new Object();
    }

    @Test
    void testPreHandlePositiveScenario() {
        request.addHeader("Customer-Number", "12345");
        request.addHeader("Type", "Type-A");
        request.addHeader("Correlation-Id", "corr-123");

        assertTrue(interceptor.preHandle(request, response, handler));
        assertEquals("12345", ThreadContext.get("CustomerNumber"));
        assertEquals("Type-A", ThreadContext.get("Type"));
        assertEquals("corr-123", ThreadContext.get("CorrelationId"));
    }

    @Test
    void testPreHandleNegativeScenario() {
        assertFalse(interceptor.preHandle(request, response, handler));
        assertNull(ThreadContext.get("CustomerNumber"));
        assertNull(ThreadContext.get("Type"));
        assertNull(ThreadContext.get("CorrelationId"));
    }

    @Test
    void testAfterCompletion() {
        request.addHeader("Customer-Number", "12345");
        request.addHeader("Type", "Type-A");
        request.addHeader("Correlation-Id", "corr-123");

        interceptor.preHandle(request, response, handler);
        interceptor.afterCompletion(request, response, handler, null);

        assertNull(ThreadContext.get("CustomerNumber"));
        assertNull(ThreadContext.get("Type"));
        assertNull(ThreadContext.get("CorrelationId"));
    }
}
LoggingInterceptorIntegrationTest
java

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.mockito.Mockito;

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
        ClientResponse clientResponse = ClientResponse.create(200).build();
        ClientRequest clientRequest = ClientRequest.create().url("http://example.com").build();

        Mockito.when(exchangeFunction.exchange(Mockito.any(ClientRequest.class))).thenReturn(Mono.just(clientResponse));

        Mono<ClientResponse> result = interceptor.filter(clientRequest, exchangeFunction);
        result.block();

        ArgumentCaptor<String> logCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(interceptor.getLogger()).info(logCaptor.capture());

        long executionTime = System.currentTimeMillis() - startTime;

        // Verify that the logger was called with the expected log message
        String loggedMessage = logCaptor.getValue();
        assertEquals("WebClient call to http://example.com took " + executionTime + "ms", loggedMessage);
    }

    @Test
    void shouldLogErrorExecutionTime() {
        long startTime = System.currentTimeMillis();
        ClientRequest clientRequest = ClientRequest.create().url("http://example.com").build();
        Exception error = new RuntimeException("Simulated error");
        Mockito.when(exchangeFunction.exchange(Mockito.any(ClientRequest.class))).thenReturn(Mono.error(error));

        Mono<ClientResponse> result = interceptor.filter(clientRequest, exchangeFunction);

        try {
            result.block();
        } catch (Exception ignored) {
        }

        ArgumentCaptor<String> logCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(interceptor.getLogger()).error(logCaptor.capture(), Mockito.any(Throwable.class));

        long executionTime = System.currentTimeMillis() - startTime;

        // Verify that the logger was called with the expected log message
        String loggedMessage = logCaptor.getValue();
        assertEquals("WebClient call to http://example.com failed after " + executionTime + "ms", loggedMessage);
    }
}
PerformanceLoggingInterceptorIntegrationTest
java

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
WebClientConfig
java

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public ExchangeFilterFunction performanceLoggingFilter() {
        return new PerformanceLoggingInterceptor();
    }

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder().filter(performanceLoggingFilter());
    }
}
WebClientConfigTest
java

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = WebClientConfigTest.TestConfig.class)
public class WebClientConfigTest {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @MockBean
    private ExchangeFilterFunction performanceLoggingFilter;

    @Configuration
    public static class TestConfig {

        @Bean
        public ExchangeFilterFunction performanceLoggingFilter() {
            return request -> {
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

        // Verify that the performanceLoggingFilter is invoked
        verify(performanceLoggingFilter).filter(Mockito.any(), Mockito.any());
    }
}
WebClientConfigIntegrationTest
java

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
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


gradle

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-logging'
    implementation 'org.springframework.boot:spring-boot-starter-webflux'
    implementation 'org.springframework.boot:spring-boot-starter-tomcat'
    implementation 'org.apache.logging.log4j:log4j-slf4j-impl:2.14.1'
    implementation 'org.apache.logging.log4j:log4j-api:2.14.1'
    implementation 'org.apache.logging.log4j:log4j-core:2.14.1'
    implementation 'org.springframework.boot:spring-boot-devtools'
    implementation 'org.springframework.boot:spring-boot-configuration-processor'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

SampleController.java:

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/sample")
public class SampleController {

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public SampleController(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @GetMapping("/invoke-api")
    public ResponseEntity<String> invokeApi() {
        WebClient webClient = webClientBuilder.baseUrl("https://api.example.com").build();

        String response = webClient.get()
            .uri("/endpoint")
            .retrieve()
            .bodyToMono(String.class)
            .block(); // Blocking for simplicity; consider using reactive code in production

        return ResponseEntity.ok(response);
    }
}



application.properties: 
======================
# Set the logging level for the root logger (change as needed)
logging.level.root=INFO

# Enable Log4j2
logging.config=classpath:log4j2.properties

# Define the base directory for log files (adjust the path as needed)
property.logDir = logs

# Set the log levels for different log categories (adjust as needed)
logging.level.activity=INFO
logging.level.application=INFO
logging.level.performance=INFO



Log4j2.properties
====================== 
# Set the root logger to ERROR and use the 'activityAppender'
rootLogger.level = error
rootLogger.appenderRefs = activityAppender
rootLogger.appenderRef.activityAppender.ref = activityAppender

# Define a rolling file appender for activity logs
appender.activity.type = RollingFile
appender.activity.name = activityAppender
appender.activity.fileName = ${logDir}/activity.log
appender.activity.filePattern = ${logDir}/activity-%d{yyyy-MM-dd}-%i.log
appender.activity.layout.type = PatternLayout
appender.activity.layout.pattern = %d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n
appender.activity.policies.type = Policies
appender.activity.policies.time.type = TimeBasedTriggeringPolicy
appender.activity.policies.time.interval = 1
appender.activity.policies.time.modulate = true
appender.activity.policies.size.type = SizeBasedTriggeringPolicy
appender.activity.policies.size.size = 10MB
appender.activity.strategy.type = DefaultRolloverStrategy
appender.activity.strategy.max = 10

# Create an AsyncLogger for 'activity'
logger.activity.name = activity
logger.activity.level = info
logger.activity.additivity = false
logger.activity.appenderRefs = activityAppender
logger.activity.appenderRef.activityAppender.ref = activityAppender
logger.activity.appenderRef.activityAppender.async = true

# Define a rolling file appender for application logs
appender.application.type = RollingFile
appender.application.name = applicationAppender
appender.application.fileName = ${logDir}/app.log
appender.application.filePattern = ${logDir}/app-%d{yyyy-MM-dd}-%i.log
appender.application.layout.type = PatternLayout
appender.application.layout.pattern = %d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n
appender.application.policies.type = Policies
appender.application.policies.time.type = TimeBasedTriggeringPolicy
appender.application.policies.time.interval = 1
appender.application.policies.time.modulate = true
appender.application.policies.size.type = SizeBasedTriggeringPolicy
appender.application.policies.size.size = 10MB
appender.application.strategy.type = DefaultRolloverStrategy
appender.application.strategy.max = 10

# Create an AsyncLogger for 'application'
logger.application.name = application
logger.application.level = info
logger.application.additivity = false
logger.application.appenderRefs = applicationAppender
logger.application.appenderRef.applicationAppender.ref = applicationAppender
logger.application.appenderRef.applicationAppender.async = true

# Define a rolling file appender for performance logs
appender.performance.type = RollingFile
appender.performance.name = performanceAppender
appender.performance.fileName = ${logDir}/perf.log
appender.performance.filePattern = ${logDir}/perf-%d{yyyy-MM-dd}-%i.log
appender.performance.layout.type = PatternLayout
appender.performance.layout.pattern = %d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n
appender.performance.policies.type = Policies
appender.performance.policies.time.type = TimeBasedTriggeringPolicy
appender.performance.policies.time.interval = 1
appender.performance.policies.time.modulate = true
appender.performance.policies.size.type = SizeBasedTriggeringPolicy
appender.performance.policies.size.size = 10MB
appender.performance.strategy.type = DefaultRolloverStrategy
appender.performance.strategy.max = 10

# Create an AsyncLogger for 'performance'
logger.performance.name = performance
logger.performance.level = info
logger.performance.additivity = false
logger.performance.appenderRefs = performanceAppender
logger.performance.appenderRef.performanceAppender.ref = performanceAppender
logger.performance.appenderRef.performanceAppender.async = true