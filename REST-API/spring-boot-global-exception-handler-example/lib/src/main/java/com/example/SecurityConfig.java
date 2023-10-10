package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

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
