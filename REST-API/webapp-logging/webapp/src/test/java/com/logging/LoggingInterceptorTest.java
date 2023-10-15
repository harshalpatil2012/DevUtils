package com.logging;
import org.springframework.web.servlet.HandlerInterceptor;
import org.apache.logging.log4j.ThreadContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;


import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

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
    void testAfterCompletion() throws Exception {
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
