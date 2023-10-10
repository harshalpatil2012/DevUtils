import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ApiCacheServiceTest {

	private ApiCacheService apiCacheService;

	@BeforeEach
	public void setUp() {
		apiCacheService = new ApiCacheService();
	}

	@Test
	public void testAddApiAndGetApiVersionForName() {
		String apiName = "exampleApi";
		String apiVersion = "1.0";

		apiCacheService.addApi(apiName, apiVersion);
		String storedApiVersion = apiCacheService.getApiVersionForName(apiName);

		assertEquals(apiVersion, storedApiVersion);
	}

	@Test
	public void testGetApiVersionForName_WithNonExistentApiName() {
		String nonExistentApiName = "nonExistentApi";

		String storedApiVersion = apiCacheService.getApiVersionForName(nonExistentApiName);

		assertEquals(null, storedApiVersion);
	}

	@Test
	public void testAddApiAndGetApiVersionForName_CaseSensitive() {
		String apiName = "exampleApi";
		String apiVersion = "1.0";

		apiCacheService.addApi(apiName, apiVersion);
		String storedApiVersion = apiCacheService.getApiVersionForName(apiName.toLowerCase()); // Check with a lowercase
																								// name

		assertEquals(apiVersion, storedApiVersion);
	}
}
