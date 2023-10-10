package com.example;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SecurityConfigIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testIntegration() {
        // Send a GET request to "/healthcheck" and check the response.
        ResponseEntity<String> response = restTemplate.getForEntity("/healthcheck", String.class);
        // Verify access to the healthcheck endpoint without authentication.

        // Send a GET request to "/protected-resource" and check the response.
        response = restTemplate.getForEntity("/protected-resource", String.class);
        // Verify lack of access to the protected-resource endpoint without authentication.
    }
}
