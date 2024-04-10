import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class RequestResponseLoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // Collect and log request headers
        Map<String, String> requestHeaders = new HashMap<>();
        Collections.list(request.getHeaderNames())
                .forEach(headerName -> requestHeaders.put(headerName, request.getHeader(headerName)));
        log.info("Request Headers: {}", requestHeaders);

        filterChain.doFilter(servletRequest, servletResponse);

        // Collect and log response headers (limited to what's available at this point)
        Map<String, String> responseHeaders = new HashMap<>();
        response.getHeaderNames().forEach(headerName -> responseHeaders.put(headerName, response.getHeader(headerName)));
        log.info("Response Headers: {}", responseHeaders);
    }
}
