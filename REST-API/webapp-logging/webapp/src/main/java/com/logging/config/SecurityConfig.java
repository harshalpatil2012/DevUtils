package com.logging.config;

import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;


@Configuration
@Import(SecurityAutoConfiguration.class)
public class SecurityConfig {
}

