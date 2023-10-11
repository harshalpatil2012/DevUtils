# Gradle-based Implementation of a Java Web Application

This is a Gradle-based implementation of a Java web application that uses Spring Boot, SLF4J, and WebClient to make REST API calls and log performance information to a `perf.log` file.

## Set Up Your Gradle Dependencies

In your `build.gradle` file, add the necessary dependencies for Spring Boot, SLF4J, Logback, and WebClient:

```gradle
plugins {
    id 'org.springframework.boot' version '2.6.0'
    id 'io.spring.dependency-management' version '1.0.11.RELEASE'
    id 'java'
}

group = 'com.example'
version = '1.0-SNAPSHOT'
sourceCompatibility = '1.8'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-logging'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
}


Create a Spring Boot Application Class
Create a class that serves as the entry point for your application and configures the WebClient:

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}


Create a PerformanceLoggingService
Create a service to log performance information using SLF4J:


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PerformanceLoggingService {
    private final Logger logger = LoggerFactory.getLogger(PerformanceLoggingService.class);

    public void logBeforeRequest(String requestUrl, String httpMethod) {import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Controller
public class ApiController {
    private final WebClient webClient;
    private final PerformanceLoggingService loggingService;

    @Autowired
    public ApiController(WebClient.Builder webClientBuilder, PerformanceLoggingService loggingService) {
        this.webClient = webClientBuilder.baseUrl("https://api.example.com").build();
        this.loggingService = loggingService;
    }

    @GetMapping("/api/resource")
    public Mono<String> fetchResource() {
        loggingService.logBeforeRequest("/api/resource", "GET");
        long startTime = System.currentTimeMillis();

        return webClient.get()
                .uri("/api/resource")
                .

        logger.info("Request URL: {} | HTTP Method: {} | Start Time: {}", requestUrl, httpMethod, System.currentTimeMillis());
    }

    public void logAfterRequest(String requestUrl, long startTime) {
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("Request URL: {} | Execution Time: {} ms", requestUrl, executionTime);
    }
}


Create a Controller
Create a REST controller that uses WebClient to make API calls and logs performance information:

