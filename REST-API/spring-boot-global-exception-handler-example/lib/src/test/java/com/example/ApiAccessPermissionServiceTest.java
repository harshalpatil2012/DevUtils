import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiAccessPermissionServiceTest {

    @MockBean
    private ApiCacheService apiCacheService;

    private ApiAccessPermissionService apiAccessPermissionService;

    @BeforeEach
    public void setUp() {
        apiAccessPermissionService = new ApiAccessPermissionService(apiCacheService);
    }

    @Test
    public void testHasAccess_WithMatchingApiVersion() {
        String apiName = "exampleApi";
        String apiVersion = "1.0";
        String storedApiVersion = "1.0";

        when(apiCacheService.getApiVersionForName(apiName)).thenReturn(storedApiVersion);

        boolean result = apiAccessPermissionService.hasAccess(null, apiName, apiVersion);

        assertTrue(result);
    }

    @Test
    public void testHasAccess_WithNonMatchingApiVersion() {
        String apiName = "exampleApi";
        String apiVersion = "1.0";
        String storedApiVersion = "2.0";

        when(apiCacheService.getApiVersionForName(apiName)).thenReturn(storedApiVersion);

        boolean result = apiAccessPermissionService.hasAccess(null, apiName, apiVersion);

        assertFalse(result);
    }

    @Test
    public void testHasAccess_WithNonExistentApiName() {
        String nonExistentApiName = "nonExistentApi";
        String apiVersion = "1.0";

        when(apiCacheService.getApiVersionForName(nonExistentApiName)).thenReturn(null);

        boolean result = apiAccessPermissionService.hasAccess(null, nonExistentApiName, apiVersion);

        assertFalse(result);
    }
}
