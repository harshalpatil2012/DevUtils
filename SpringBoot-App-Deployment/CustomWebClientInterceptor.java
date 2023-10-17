import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

In this class, we create a method called addCustomHeaders that returns an ExchangeFilterFunction. This function modifies the headers of the request and then passes it to the next step in the WebClient's exchange pipeline.

@Component
public class CustomWebClientInterceptor {

    public ExchangeFilterFunction addCustomHeaders() {
        return (request, next) -> {
            // Modify the request's headers here
            HttpHeaders headers = request.headers().asHttpHeaders();
            headers.add("Custom-Header", "Your-Header-Value");

            // Continue with the modified request
            return next.exchange(request);
        };
    }
}


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder(CustomWebClientInterceptor customWebClientInterceptor) {
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .filter(customWebClientInterceptor.addCustomHeaders());
    }
}


import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class MyService {
    private final WebClient webClient;

    public MyService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://example.com").build();
    }

    public Mono<String> fetchData() {
        return webClient.get()
                .uri("/api/data")
                .retrieve()
                .bodyToMono(String.class);
    }
}
