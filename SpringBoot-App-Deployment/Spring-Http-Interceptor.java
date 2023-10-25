package com.example;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface ReactiveResponseInterceptor {

    Mono<ServerWebExchange> filter(ServerWebExchange exchange, WebFilterChain chain);
}

package com.example;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface ReactiveRequestInterceptor {

    Mono<ServerWebExchange> filter(ServerWebExchange exchange, WebFilterChain chain);
}

package com.example;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.reactive.filter.WebFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;

@Configuration
public class WebFluxConfig {

    @Bean
    public WebFilter requestResponseFilter(ReactiveRequestInterceptor requestInterceptor, ReactiveResponseInterceptor responseInterceptor) {
        return new WebFilter() {

            @Override
            public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
                return requestInterceptor.filter(exchange, chain)
                        .then(responseInterceptor.filter(exchange, chain));
            }
        };
    }

    @Bean
    public WebClient webClient(HttpClient httpClient) {
        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }
}

package com.example;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class SimpleReactiveRequestInterceptor implements ReactiveRequestInterceptor {

    @Override
    public Mono<ServerWebExchange> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Update the request URI
        String newUri = exchange.getRequest().getURI().toString() + "/updated";
        ServerWebExchange updatedRequest = exchange.mutate().request(exchange.getRequest().mutate().uri(newUri).build()).build();

        // Update the request path
        String newPath = updatedRequest.getRequest().getPath() + "/updated";
        updatedRequest = updatedRequest.mutate().request(updatedRequest.getRequest().mutate().path(newPath).build()).build();

        // Update the request query parameters
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", "John Doe");
        queryParams.put("age", "30");
        updatedRequest = updatedRequest.mutate().queryParams(queryParams).build();

        // Update the request payload
        Mono<String> requestBody = updatedRequest.getRequest().getBodyToMono(String.class);
        requestBody = requestBody.map(body -> body.replace("old", "new"));

        // Wrap the request object
        updatedRequest = updatedRequest.mutate().request(updatedRequest.getRequest().mutate().body(requestBody).build()).build();

        // Continue processing the request
        return chain.filter(updatedRequest);
    }
}


package com.example;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class SimpleReactiveResponseInterceptor implements ReactiveResponseInterceptor {

    @Override
    public Mono<ServerWebExchange> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Update the response payload
        Mono<String> responseBody = exchange.getResponse().getBodyToMono(String.class);
        responseBody = responseBody.map(body -> body.replace("old", "new"));

        // Wrap the response object
        ServerWebExchange updatedResponse = exchange.mutate().response(exchange.getResponse().mutate().body(responseBody).build()).build();

        // Continue processing the request
        return chain.filter(updatedResponse);
    }
}


package com.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.server.ServerWebExchange;

public class SimpleReactiveRequestInterceptorTest {

    private final SimpleReactiveRequestInterceptor interceptor = new SimpleReactiveRequestInterceptor();

    @Test
    public void testFilter_withValidRequest_shouldUpdateRequest() {
        // Arrange
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        when(exchange.getRequest().getURI().toString()).thenReturn("/api/v1/users/1");

        // Act
        ServerWebExchange updatedRequest = interceptor.filter(exchange, mock(WebFilterChain.class)).block();

        // Assert
        assertThat(updatedRequest.getRequest().getURI().toString()).isEqualTo("/api/v1/users/1/updated");
        assertThat(updatedRequest.getRequest().getPath()).isEqualTo("/users/1/updated");
        assertThat(updatedRequest.getRequest().getQueryParams()).containsEntry("name", "John Doe");
        assertThat(updatedRequest.getRequest().getQueryParams()).containsEntry("age", "30");
        assertThat(updatedRequest.getRequest().getBody().toMono().block()).isEqualTo("{\"name\": \"John Doe\", \"age\": 30}");
    }

    @Test
    public void testFilter_withInvalidRequest_shouldReturnOriginalRequest() {
        // Arrange
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        when(exchange.getRequest().getURI().toString()).thenReturn("/invalid/uri");

        // Act
        ServerWebExchange updatedRequest = interceptor.filter(exchange, mock(WebFilterChain.class)).block();

        // Assert
        assertThat(updatedRequest).isSameAs(exchange);
    }
	
	 @Test
    public void testFilter_withPostRequest_shouldUpdateRequest() {
        // Arrange
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        when(exchange.getRequest().getMethod()).thenReturn(HttpMethod.POST);
        when(exchange.getRequest().getBody().toMono().block()).isEqualTo("{\"name\": \"John Doe\", \"age\": 30}");

        // Act
        ServerWebExchange updatedRequest = interceptor.filter(exchange, mock(WebFilterChain.class)).block();

        // Assert
        assertThat(updatedRequest.getRequest().getBody().toMono().block()).isEqualTo("{\"name\": \"John Doe\", \"age\": 30, \"updated\": true}");
    }

    @Test
    public void testFilter_withPutRequest_shouldUpdateRequest() {
        // Arrange
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        when(exchange.getRequest().getMethod()).thenReturn(HttpMethod.PUT);
        when(exchange.getRequest().getBody().toMono().block()).isEqualTo("{\"name\": \"John Doe\", \"age\": 30}");

        // Act
        ServerWebExchange updatedRequest = interceptor.filter(exchange, mock(WebFilterChain.class)).block();

        // Assert
        assertThat(updatedRequest.getRequest().getBody().toMono().block()).isEqualTo("{\"name\": \"John Doe\", \"age\": 30, \"updated\": true}");
    }

    @Test
    public void testFilter_withDeleteRequest_shouldUpdateRequest() {
        // Arrange
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        when(exchange.getRequest().getMethod()).thenReturn(HttpMethod.DELETE);

        // Act
        ServerWebExchange updatedRequest = interceptor.filter(exchange, mock(WebFilterChain.class)).block();

        // Assert
        assertThat(updatedRequest).isSameAs(exchange);
    }

    @Test
    public void testFilter_withRequestHeader_shouldUpdateRequestHeader() {
        // Arrange
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        when(exchange.getRequest().getHeaders().getFirst("Authorization")).thenReturn("Bearer my-token");

        // Act
        ServerWebExchange updatedRequest = interceptor.filter(exchange, mock(WebFilterChain.class)).block();

        // Assert
        assertThat(updatedRequest.getRequest().getHeaders().getFirst("Authorization")).isEqualTo("Bearer my-updated-token");
    }

    @Test
    public void testFilter_withRequestBody_shouldUpdateRequestBody() {
        // Arrange
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        when(exchange.getRequest().getBody().toMono().block()).isEqualTo("{\"name\": \"John Doe\", \"age\": 30}");

        // Act
        ServerWebExchange updatedRequest = interceptor.filter(exchange, mock(WebFilterChain.class)).block();

        // Assert
        assertThat(updatedRequest.getRequest().getBody().toMono().block()).isEqualTo("{\"name\": \"John Doe\", \"age\": 30, \"updated\": true}");
    }
}


package com.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.server.ServerWebExchange;

public class SimpleReactiveResponseInterceptorTest {

    private final SimpleReactiveResponseInterceptor interceptor = new SimpleReactiveResponseInterceptor();

    @Test
    public void testFilter_withValidResponse_shouldUpdateResponse() {
        // Arrange
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        when(exchange.getResponse().getBodyToMono().block()).isEqualTo("{\"name\": \"John Doe\", \"age\": 30}");

        // Act
        ServerWebExchange updatedResponse = interceptor.filter(exchange, mock(WebFilterChain.class)).block();

        // Assert
        assertThat(updatedResponse.getResponse().getBodyToMono().block()).isEqualTo("{\"name\": \"John Doe\", \"age\": 30, \"updated\": true}");
    }

    @Test
    public void testFilter_withInvalidResponse_shouldReturnOriginalResponse() {
        // Arrange
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        when(exchange.getResponse().getBodyToMono().block()).isNull();

        // Act
        ServerWebExchange updatedResponse = interceptor.filter(exchange, mock(WebFilterChain.class)).block();

        // Assert
        assertThat(updatedResponse).isSameAs(exchange);
    }
	@Test
    public void testFilter_with200StatusCode_shouldUpdateResponse() {
        // Arrange
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        when(exchange.getResponse().getStatusCode()).thenReturn(HttpStatus.OK);
        when(exchange.getResponse().getBodyToMono().block()).isEqualTo("{\"name\": \"John Doe\", \"age\": 30}");

        // Act
        ServerWebExchange updatedResponse = interceptor.filter(exchange, mock(WebFilterChain.class)).block();

        // Assert
        assertThat(updatedResponse.getResponse().getBodyToMono().block()).isEqualTo("{\"name\": \"John Doe\", \"age\": 30, \"updated\": true}");
    }

    @Test
    public void testFilter_with201StatusCode_shouldUpdateResponse() {
        // Arrange
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        when(exchange.getResponse().getStatusCode()).thenReturn(HttpStatus.CREATED);
        when(exchange.getResponse().getBodyToMono().block()).isEqualTo("{\"name\": \"John Doe\", \"age\": 30}");

        // Act
        ServerWebExchange updatedResponse = interceptor.filter(exchange, mock(WebFilterChain.class)).block();

        // Assert
        assertThat(updatedResponse.getResponse().getBodyToMono().block()).isEqualTo("{\"name\": \"John Doe\", \"age\": 30, \"updated\": true}");
    }

    @Test
    public void testFilter_with400StatusCode_shouldUpdateResponse() {
        // Arrange
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        when(exchange.getResponse().getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(exchange.getResponse().getBodyToMono().block()).isEqualTo("{\"error\": \"Invalid request\"}");

        // Act
        ServerWebExchange updatedResponse = interceptor.filter(exchange, mock(WebFilterChain.class)).block();

        // Assert
        assertThat(updatedResponse.getResponse().getBodyToMono().block()).isEqualTo("{\"error\": \"Invalid request\", \"updated\": true}");
    }

    @Test
    public void testFilter_with500StatusCode_shouldUpdateResponse() {
        // Arrange
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        when(exchange.getResponse().getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        when(exchange.getResponse().getBodyToMono().block()).isEqualTo("An error occurred");

        // Act
        ServerWebExchange updatedResponse = interceptor.filter(exchange, mock(WebFilterChain.class)).block();

        // Assert
        assertThat(updatedResponse.getResponse().getBodyToMono().block()).isEqualTo("An error occurred");
    }

    @Test
    public void testFilter_withResponseHeader_shouldUpdateResponseHeader() {
        // Arrange
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        when(exchange.getResponse().getHeaders().getFirst("Location")).thenReturn("/api/v1/users/1");

        // Act
        ServerWebExchange updatedResponse = interceptor.filter(exchange, mock(WebFilterChain.class)).block();

        // Assert
        assertThat(updatedResponse.getResponse().getHeaders().getFirst("Location")).isEqualTo("/api/v1/users/1/updated");
    }

    @Test
    public void testFilter_withResponseBody_shouldUpdateResponseBody() {
        // Arrange
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        when(exchange.getResponse().getBodyToMono().block()).isEqualTo("{\"name\": \"John Doe\", \"age\": 30}");

        // Act
        ServerWebExchange updatedResponse = interceptor.filter(exchange, mock(WebFilterChain.class)).block();

        // Assert
        assertThat(updatedResponse.getResponse().getBodyToMono().block()).isEqualTo("{\"name\": \"John Doe\", \"age\": 30, \"updated\": true}");
    }

}


package com.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.reactive.server.WebTestClient.BodySpec;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@TestPropertySource(properties = "spring.web.reactive.filter.add=requestResponseFilter")
public class WebFluxIntegrationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testRequestResponseFilter_withValidRequest_shouldUpdateRequestAndResponse() {
        // Act
        BodySpec<String> response = webTestClient
                .get()
                .uri("/api/v1/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class);

        // Assert
        assertThat(response.extract().body().block()).contains("John Doe").contains("30").contains("updated");
    }

    @Test
    public void testRequestResponseFilter_withInvalidRequest_shouldReturn404() {
        // Act
        webTestClient
                .get()
                .uri("/invalid/uri")
                .exchange()
                .expectStatus().isNotFound();
    }
	
	 @Test
    public void testRequestResponseFilter_withJsonContentType_shouldUpdateRequestAndResponse() {
        // Act
        BodySpec<String> response = webTestClient
                .get()
                .uri("/api/v1/users/1")
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class);

        // Assert
        assertThat(response.extract().body().block()).contains("John Doe").contains("30").contains("updated");
    }

    @Test
    public void testRequestResponseFilter_withXmlContentType_shouldUpdateRequestAndResponse() {
        // Act
        BodySpec<String> response = webTestClient
                .get()
                .uri("/api/v1/users/1")
                .header("Content-Type", "application/xml")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class);

        // Assert
        assertThat(response.extract().body().block()).contains("John Doe").contains("30").contains("updated");
    }

    @Test
    public void testRequestResponseFilter_withLargeContent_shouldUpdateRequestAndResponse() {
        // Create a large request body
        String largeRequestBody = "This is a large request body.";

        // Act
        BodySpec<String> response = webTestClient
                .post()
                .uri("/api/v1/users")
                .header("Content-Type", "application/json")
                .bodyValue(largeRequestBody)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class);

        // Assert
        assertThat(response.extract().body().block()).contains(largeRequestBody);
    }

    @Test
    public void testRequestResponseFilter_withConcurrentRequests_shouldUpdateRequestAndResponse() {
        // Create multiple concurrent requests
        Mono<BodySpec<String>>[] responses = new Mono[10];
        for (int i = 0; i < 10; i++) {
            responses[i] = webTestClient
                    .get()
                    .uri("/api/v1/users/1")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(String.class);
        }

        // Wait for all requests to complete
        Mono.when(responses).block();

        // Assert that all responses were updated correctly
        for (BodySpec<String> response : responses) {
            assertThat(response.extract().body().block()).contains("John Doe").contains("30").contains("updated");
        }
    }

    @Test
    public void testRequestResponseFilter_withErrorCondition_shouldLogAndReturn500() {
        // Arrange
        // Introduce an error condition in the interceptor

        // Act
        BodySpec<String> response = webTestClient
                .get()
                .uri("/api/v1/users/1")
                .exchange()
                .expectStatus().is500();

        // Assert
        assertThat(response.extract().body().block()).contains("An error occurred");
    }
}

