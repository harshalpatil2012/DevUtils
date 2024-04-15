logging:
  level:
    org:
      apache:
        coyote:
          http11: DEBUG

          import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.stream.Collectors;

@Component
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // Wrap the request and response to cache content for logging
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        // Proceed with the chain
        chain.doFilter(wrappedRequest, wrappedResponse);

        // Log request details
        logRequest(wrappedRequest);
        // Log response details
        logResponse(wrappedResponse);

        // Important: copy content from the wrapped response to the actual response
        wrappedResponse.copyBodyToResponse();
    }

    private void logRequest(ContentCachingRequestWrapper request) throws IOException {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String queryString = request.getQueryString();
        String headers = Collections.list(request.getHeaderNames()).stream()
                .map(headerName -> headerName + ": " + Collections.list(request.getHeaders(headerName)))
                .collect(Collectors.joining(", "));
        String body = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);

        System.out.println("Request URI: " + uri + (queryString != null ? "?" + queryString : ""));
        System.out.println("Method: " + method);
        System.out.println("Headers: " + headers);
        System.out.println("Body: " + body);
    }

    private void logResponse(ContentCachingResponseWrapper response) throws IOException {
        int status = response.getStatus();
        String headers = response.getHeaderNames().stream()
                .map(headerName -> headerName + ": " + response.getHeaders(headerName))
                .collect(Collectors.joining(", "));
        String body = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);

        System.out.println("Response Status: " + status);
        System.out.println("Headers: " + headers);
        System.out.println("Body: " + body);
    }
}


logging:
  level:
    org:
      apache:
        http: DEBUG
        http.headers: DEBUG
        http.wire: DEBUG
logging:
  level:
    org:
      springframework:
        web:
          reactive:
            function:
              client:
                ExchangeFunctions: DEBUG

