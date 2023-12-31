package com.logging.config;

import com.logging.PerformanceLoggingInterceptor;
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
