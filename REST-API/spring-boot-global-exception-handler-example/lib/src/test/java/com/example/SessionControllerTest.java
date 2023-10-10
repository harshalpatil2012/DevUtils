import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(SessionController.class)
@AutoConfigureMockMvc
public class SessionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SessionService sessionService;

	@BeforeEach
	void setUp() {
		// Configure mock behavior for sessionService as needed
	}

	@Test
	public void testCreateSession() throws Exception {
		// Use mockMvc to perform HTTP POST request and verify the response
		mockMvc.perform(MockMvcRequestBuilders.post("/sessions").contentType("application/json").content(
				"{\"user_id\": 1, \"session_start\": \"2023-01-01T12:00:00\", \"session_end\": \"2023-01-01T13:00:00\"}"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testGetSession() throws Exception {
		// Use mockMvc to perform HTTP GET request and verify the response
		mockMvc.perform(MockMvcRequestBuilders.get("/sessions/1")).andExpect(MockMvcResultMatchers.status().isOk());
	}
}
