To print the request and response JSON in a Zuul Gateway API, you can use a custom Zuul filter as shown in the previous response. However, instead of just logging, you can extract the request and response data, convert it to JSON, and print it. Here's an example of how to do this:



1. Create a custom Zuul filter to log the request and response JSON:

```java
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.Charset;

@Component
public class LoggingFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "post"; // This filter runs after the request has been routed.
    }

    @Override
    public int filterOrder() {
        return 1; // Set the order in which this filter runs.
    }

    @Override
    public boolean shouldFilter() {
        return true; // Enable this filter for all requests.
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();

        // Log the request JSON
        InputStream requestStream = ctx.getRequest().getInputStream();
        String requestJson = StreamUtils.copyToString(requestStream, Charset.forName("UTF-8"));
        System.out.println("Request JSON: " + requestJson);

        // Log the response JSON
        InputStream responseStream = ctx.getResponseDataStream();
        String responseJson = StreamUtils.copyToString(responseStream, Charset.forName("UTF-8"));
        System.out.println("Response JSON: " + responseJson);

        // Set the response data back to the original stream so that it can be forwarded to the client
        ctx.setResponseBody(responseJson);

        return null;
    }
}
```

This filter will capture the request and response JSON and print it to the console. Make sure to configure the filter order and type appropriately based on when you want this filter to run.

2. Register this filter in Zuul Gateway application, as mentioned in the previous response.

3. When a request passes through  Zuul Gateway, this filter will log the request and response JSON to the console.

Make sure you have the necessary dependencies for Spring Cloud and Zuul configured in project. Additionally, you should configure a logging framework like Log4j or SLF4J to control where the logs are written.