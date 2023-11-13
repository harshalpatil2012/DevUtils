package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class ApiAccessPermissionService {

	private final ApiCacheService apiCacheService;

	@Autowired
	public ApiAccessPermissionService(ApiCacheService apiCacheService) {
		this.apiCacheService = apiCacheService;
	}

	/**
	 * Now, you can use the ApiAccessPermissionService in SecurityConfig to
	 * control access to dynamic /proxy/{api-name}/{api-version} endpoints:
	 * 
	 * .antMatchers("/proxy/{api-name}/{api-version}/**")
	 * .access("@apiAccessPermissionService.hasAccess(authentication, #apiName,
	 * #apiVersion)") This configuration will dynamically check if the user has
	 * access to the endpoint based on the values retrieved from the ApiCacheService
	 * and allow or deny access accordingly.
	 * 
	 * @param authentication
	 * @param apiName
	 * @param apiVersion
	 * @return
	 */

	public boolean hasAccess(Authentication authentication, String apiName, String apiVersion) {
		// Get the stored API version for the provided API name
		String storedApiVersion = apiCacheService.getApiVersionForName(apiName);

		// Check if the user has access based on the stored API version
		return storedApiVersion != null && storedApiVersion.equals(apiVersion);
	}
}
