import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/sample")
public class SampleController {

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public SampleController(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @GetMapping("/invoke-api")
    public ResponseEntity<String> invokeApi() {
        WebClient webClient = webClientBuilder.baseUrl("https://api.example.com").build();

        String response = webClient.get()
                .uri("/endpoint")
                .retrieve()
                .bodyToMono(String.class)
                .block(); // Blocking for simplicity; consider using reactive code in production

        return ResponseEntity.ok(response);
    }
}
