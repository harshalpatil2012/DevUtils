package com.example.db;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomCsrfTokenRepository implements CsrfTokenRepository {

    // We may need to inject a repository or service to interact with your database
    private final CustomSessionRepository customSessionRepository;

    public CustomCsrfTokenRepository(CustomSessionRepository customSessionRepository) {
        this.customSessionRepository = customSessionRepository;
    }

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        // Generate and return a custom CSRF token
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        // Save the CSRF token in your custom session table
        customSessionRepository.saveCsrfToken(token.getToken());
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        // Load the CSRF token from your custom session table
        String tokenValue = customSessionRepository.loadCsrfToken();
        return new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", tokenValue);
    }
}
