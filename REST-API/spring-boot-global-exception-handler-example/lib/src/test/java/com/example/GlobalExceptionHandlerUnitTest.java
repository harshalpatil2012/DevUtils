import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.WebClientRequestException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GlobalExceptionHandler.class)
public class GlobalExceptionHandlerUnitTest {

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private ItemService itemService;

	@Test
	public void testHandleHttpClientErrorException() throws Exception {
		when(itemService.getAllItems()).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found"));

		mockMvc.perform(get("/items")).andExpect(status().isNotFound());
	}

	@Test
	public void testHandleHttpServerErrorException() throws Exception {
		when(itemService.getAllItems())
				.thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));

		mockMvc.perform(get("/items")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testHandleResourceAccessException() throws Exception {
		when(itemService.getAllItems()).thenThrow(new ResourceAccessException("Resource Access Exception"));

		mockMvc.perform(get("/items")).andExpect(status().isServiceUnavailable());
	}

	@Test
	public void testHandleWebClientRequestException() throws Exception {
		when(itemService.getAllItems()).thenThrow(new WebClientRequestException("WebClient Request Exception"));

		mockMvc.perform(get("/items")).andExpect(status().isBadGateway());
	}
}
