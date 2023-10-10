import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class SecurityConfigIntegrationTest {

	@Container
	private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>();

	@DynamicPropertySource
	static void setDataSourceProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
		registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
		registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
	}

	@Autowired
	private MockMvc mockMvc;

	@BeforeAll
	static void startContainers() {
		postgreSQLContainer.start();
	}

	@Test
	public void testAccessToSecuredEndpoint() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/securedEndpoint")).andExpect(status().isOk()); // Assert that
																									// access is allowed
																									// when
																									// authenticated.
	}

	@Test
	public void testAccessToUnsecuredEndpoint() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/unsecuredEndpoint")).andExpect(status().isOk()); // Assert that
																										// access is
																										// allowed
																										// without
																										// authentication.
	}

	// Add more integration tests to cover various security scenarios.
}
