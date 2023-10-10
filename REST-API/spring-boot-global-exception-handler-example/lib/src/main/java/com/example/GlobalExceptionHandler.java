package com.example;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.WebClientRequestException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> handleHttpClientErrorException(HttpClientErrorException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<String> handleHttpServerErrorException(HttpServerErrorException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<String> handleResourceAccessException(ResourceAccessException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Resource Access Error");
    }

    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<String> handleWebClientRequestException(WebClientRequestException ex) {
        if (ex instanceof HttpClientErrorException) {
            HttpClientErrorException httpClientError = (HttpClientErrorException) ex;
            return ResponseEntity.status(httpClientError.getStatusCode()).body(httpClientError.getResponseBodyAsString());
        } else if (ex instanceof HttpServerErrorException) {
            HttpServerErrorException httpServerError = (HttpServerErrorException) ex;
            return ResponseEntity.status(httpServerError.getStatusCode()).body(httpServerError.getResponseBodyAsString());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("WebClient Request Error");
        }
    }
}
