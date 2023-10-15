package com.logging.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("security.csp")
@Data
public class CspProperties {
	private String defaultSrc;
	private String scriptSrc;
	private String styleSrc;
	private String imgSrc;
	private String fontSrc;
	private String frameSrc;
	private String mediaSrc;
	private String connectSrc;
	private String childSrc;
	private String pluginSrc;
}
