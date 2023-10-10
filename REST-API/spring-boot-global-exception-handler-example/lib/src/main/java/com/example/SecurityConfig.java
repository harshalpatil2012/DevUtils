import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

/**
 * the CsrfToken and CsrfTokenRepository classes are part of the Spring Security
 * framework. They are provided by Spring Security to manage Cross-Site Request
 * Forgery (CSRF) protection. Here's a brief explanation of these classes:
 * 
 * CsrfToken: This is an interface in Spring Security that represents a CSRF
 * token. It typically holds the token value and the parameter name used to
 * submit the token with HTTP requests.
 * 
 * CsrfTokenRepository: This is another interface in Spring Security that
 * manages the generation, saving, and loading of CSRF tokens. It defines
 * methods for generating a token, saving it to an HTTP response, and loading it
 * from an HTTP request.
 * 
 * The code you provided in your previous message is an example of how you can
 * create a custom implementation of the CsrfTokenRepository interface to handle
 * CSRF tokens in a way that suits your application's specific requirements. In
 * the custom implementation, you can define how tokens are generated, saved,
 * and loaded based on your needs.
 * 
 * Spring Security provides default implementations of these classes, such as
 * HttpSessionCsrfTokenRepository for session-based CSRF token management, but
 * you can create custom implementations to tailor the behavior to your
 * application. Spring Security provides default implementations of these
 * classes, such as HttpSessionCsrfTokenRepository for session-based CSRF token
 * management, but you can create custom implementations to tailor the behavior
 * to your application.
 * 
 * 
 * 
 * 
 * 
 * @author harshal
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/healthcheck").permitAll().antMatchers("/proxy/init").permitAll()
				.antMatchers("/proxy/{api-name}/{api-version}/**")
				.access("@apiAccessPermissionService.hasAccess(authentication, #apiName, #apiVersion)").and()
				.authorizeRequests().anyRequest().authenticated().and().logout().permitAll().and()
				.httpStrictTransportSecurity().maxAgeInSeconds(31536000).includeSubDomains(true).and().frameOptions()
				.deny().and().xssProtection().block(true).and().and().csrf().csrfTokenRepository(csrfTokenRepository())
				.and().addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class);
	}

	@Bean
	public CsrfTokenRepository csrfTokenRepository() {
		HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
		repository.setHeaderName("X-XSRF-TOKEN"); // Custom CSRF header name
		return repository;
	}
}
