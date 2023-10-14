package com.logging;
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
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ThreadContext.clearAll();
    }
}
