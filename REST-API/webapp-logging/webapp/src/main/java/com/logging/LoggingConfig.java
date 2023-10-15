package com.logging;

import org.springframework.web.servlet.HandlerInterceptor;
import org.apache.logging.log4j.ThreadContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

	public void LoggingConfig() {

	}

	@Bean
	public LoggingInterceptor loggingInterceptor() {
		return new LoggingInterceptor();
	}
}
