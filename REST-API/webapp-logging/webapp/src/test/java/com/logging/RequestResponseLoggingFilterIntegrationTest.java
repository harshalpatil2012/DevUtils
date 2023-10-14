package com.logging;
import org.springframework.web.servlet.HandlerInterceptor;
import org.apache.logging.log4j.ThreadContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

public class RequestResponseLoggingFilterIntegrationTest {

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