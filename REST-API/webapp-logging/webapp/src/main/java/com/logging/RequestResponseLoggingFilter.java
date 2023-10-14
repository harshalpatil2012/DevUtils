package com.logging;

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
