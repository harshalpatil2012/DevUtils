package com.example;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class ApiCacheService {

    private final Map<String, String> apiCache = new HashMap<>();

    public void addApi(String apiName, String apiVersion) {
        apiCache.put(apiName, apiVersion);
    }

    public String getApiVersionForName(String apiName) {
        return apiCache.get(apiName);
    }
}
