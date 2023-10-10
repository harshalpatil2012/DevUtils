package com.example;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class GlobalExceptionHandlerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ItemService itemService;

    @Test
    public void testHandleHttpClientErrorException() throws Exception {
        when(itemService.getAllItems()).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found"));

        mockMvc.perform(get("/items"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testHandleHttpServerErrorException() throws Exception {
        when(itemService.getAllItems()).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));

        mockMvc.perform(get("/items"))
                .andExpect(status().isInternalServerError());
    }

    // Add more unit tests for ResourceAccessException and WebClientRequestException
}
