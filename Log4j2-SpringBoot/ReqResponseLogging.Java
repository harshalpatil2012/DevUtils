import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RequestResponseLoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // Log request headers
        Collections.list(request.getHeaderNames())
                .forEach(headerName -> log.info("Request Header: {} = {}", headerName, request.getHeader(headerName)));

        filterChain.doFilter(servletRequest, servletResponse);

        // Log response headers (limited to what's available at this point)
        response.getHeaderNames().forEach(headerName -> log.info("Response Header: {} = {}", headerName, response.getHeader(headerName)));
    }
}
