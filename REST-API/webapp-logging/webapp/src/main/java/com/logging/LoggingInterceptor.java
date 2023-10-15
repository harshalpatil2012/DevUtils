package com.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.apache.logging.log4j.ThreadContext;

public class LoggingInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String customerNumber = "12388";
		String type = "REST";
		String correlationId = "w234-3223-2323-2323";
		String jSessionId = "J-99889889";
		String sessionId = "S-12133333";

		if (customerNumber != null) {
			ThreadContext.put("CustomerNumber", customerNumber);
		}
		if (type != null) {
			ThreadContext.put("Type", type);
		}
		if (correlationId != null) {
			ThreadContext.put("CorrelationId", correlationId);
		}
		ThreadContext.put("JSessionId", jSessionId);
		ThreadContext.put("SessionId", sessionId);

		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		ThreadContext.clearAll();
	}

}
