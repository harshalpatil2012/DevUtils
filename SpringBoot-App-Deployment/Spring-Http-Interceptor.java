Interceptor for http request and response

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class RequestInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logRequest(request);

        // Update the request URL path
        String newPath = request.getPathInfo() + "/updated";
        request.setAttribute(HttpServletRequest.REQUEST_URI, newPath);

        // Update the request parameters
        Map<String, String[]> params = request.getParameterMap();
        params.put("updatedParam", new String[]{"updatedValue"});

        // Update the request payload
        String requestBody = IOUtils.toString(request.getInputStream());

        // Modify the request body as needed
        requestBody = requestBody.replace("old", "new");

        // Wrap the request object
        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request);
        requestWrapper.setContent(requestBody.getBytes());
        request = requestWrapper;

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

    private void logRequest(HttpServletRequest request) throws IOException {
        String requestBody = IOUtils.toString(request.getInputStream());

        LOGGER.info("Request: {} {} {} {}", request.getMethod(), request.getRequestURI(), request.getContentType(), requestBody);
    }
}

public class ResponseInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        byte[] responseBody = IOUtils.toByteArray(response.getOutputStream());

        // Modify the response body as needed
        responseBody = responseBody.replace("old", "new");

        // Wrap the response object
        HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(response);
        responseWrapper.setContent(responseBody);
        response = responseWrapper;

        LOGGER.info("Response: {} {} {} {}", response.getStatus(), response.getContentType(), response.getHeader("Content-Length"), responseBody);
    }
}



Using reacitive Spring:

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

public class ReactiveRequestInterceptor implements HandlerFilterFunction<ServerRequest, ServerResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveRequestInterceptor.class);

    // Update the request URL path
        String newPath = request.uri() + "/updated";
        ServerRequest updatedRequest = ServerRequest.create(request.exchange(), newPath);

        // Update the request parameters
        updatedRequest = updatedRequest.queryParam("updatedParam", "updatedValue");

        // Update the request payload
        Mono<String> requestBody = request.bodyToMono(String.class);
        requestBody = requestBody.map(body -> body.replace("old", "new"));

        // Wrap the request object
        updatedRequest = updatedRequest.body(requestBody);

        // Continue processing the request
        return next.filter(updatedRequest);
}

public class ReactiveResponseInterceptor implements HandlerFilterFunction<ServerRequest, ServerResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveResponseInterceptor.class);

    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFilterFunction<ServerRequest, ServerResponse> next) {

        // Continue processing the request
        return next.filter(request).map(response -> {

            // Update the response payload
            Mono<String> responseBody = response.bodyToMono(String.class);
            responseBody = responseBody.map(body -> body.replace("old", "new"));

            // Wrap the response object
            ServerResponse updatedResponse = ServerResponse.create(response.statusCode(), response.headers(), responseBody);

            return updatedResponse;
        });
    }
}

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ServerWebExchange;

public class RequestResponseFilter extends OncePerRequestFilter {

    private final ReactiveRequestInterceptor requestInterceptor;
    private final ReactiveResponseInterceptor responseInterceptor;

    public RequestResponseFilter(ReactiveRequestInterceptor requestInterceptor, ReactiveResponseInterceptor responseInterceptor) {
        this.requestInterceptor = requestInterceptor;
        this.responseInterceptor = responseInterceptor;
    }

    @Override
    protected Mono<Void> filterInternal(ServerWebExchange exchange, Chain chain) {
        return requestInterceptor.filter(exchange, chain)
                .then(responseInterceptor.filter(exchange, chain));
    }
}

import org.springframework.boot.web.reactive.filter.WebFilterChain;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;

//Once we registered the RequestResponseFilter, it will be executed before all other filters, ensuring that the request and response interceptors are        // executed before everything else.
@Configuration
public class WebFluxConfig implements WebFluxConfigurer {

    private final ReactiveRequestInterceptor requestInterceptor;
    private final ReactiveResponseInterceptor responseInterceptor;

    public WebFluxConfig(ReactiveRequestInterceptor requestInterceptor, ReactiveResponseInterceptor responseInterceptor) {
        this.requestInterceptor = requestInterceptor;
        this.responseInterceptor = responseInterceptor;
    }

    @Override
    public void addFilters(WebFilterRegistry registry) {
        registry.addFilterFirst(new RequestResponseFilter(requestInterceptor, responseInterceptor));
    }
}