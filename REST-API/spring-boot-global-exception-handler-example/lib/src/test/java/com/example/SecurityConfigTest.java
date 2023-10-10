import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testAccessToSecuredEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/securedEndpoint"))
                .andExpect(status().isOk()); // Assert that access is allowed when authenticated.
    }

    @Test
    public void testAccessToUnsecuredEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/unsecuredEndpoint"))
                .andExpect(status().isOk()); // Assert that access is allowed without authentication.
    }

    // Add more integration tests to cover various security scenarios.
}
