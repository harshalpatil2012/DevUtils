package com.logging;

import com.logging.LoggingInterceptor;
import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
        assertNotNull(ThreadContext.get("CustomerNumber"));
        assertNotNull(ThreadContext.get("Type"));
        assertNotNull(ThreadContext.get("CorrelationId"));

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

        Mockito.verifyNoMoreInteractions(ThreadContext.class);
    }

    @Test
    void testAfterCompletion() throws Exception {
        request.addHeader("Customer-Number", "12345");
        request.addHeader("Type", "Type-A");
        request.addHeader("Correlation-Id", "corr-123");

        interceptor.preHandle(request, response, handler);
        interceptor.afterCompletion(request, response, handler, null);

        // Check if values were cleared from the ThreadContext
        assertNull(ThreadContext.get("CustomerNumber"));
        assertNull(ThreadContext.get("Type"));
        assertNull(ThreadContext.get("CorrelationId"));

        //Mockito.verify(ThreadContext.class).clear();
    }
}
