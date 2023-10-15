package com.logging.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
	private final CspProperties cspProperties;

	public SecurityConfig(CspProperties cspProperties) {
		this.cspProperties = cspProperties;
	}

	protected void configure(HttpSecurity http) throws Exception {
		http.headers().contentSecurityPolicy(createCspHeader());
    }

    private String createCspHeader() {
        return "default-src " + cspProperties.getDefaultSrc() +
                "; script-src " + cspProperties.getScriptSrc() +
                "; style-src " + cspProperties.getStyleSrc() +
                "; img-src " + cspProperties.getImgSrc() +
                "; font-src " + cspProperties.getFontSrc() +
                "; frame-src " + cspProperties.getFrameSrc() +
                "; media-src " + cspProperties.getMediaSrc() +
                "; connect-src " + cspProperties.getConnectSrc() +
                "; child-src " + cspProperties.getChildSrc() +
                "; plugin-src " + cspProperties.getPluginSrc();
    }
}
