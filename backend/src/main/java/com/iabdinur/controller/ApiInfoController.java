package com.iabdinur.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class ApiInfoController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> apiInfo = new LinkedHashMap<>();
        apiInfo.put("name", "Blog API");
        apiInfo.put("version", "1.0.0");
        apiInfo.put("baseUrl", "/api/v1");
        apiInfo.put("description", "RESTful API for the blog platform");

        List<Map<String, Object>> endpoints = new ArrayList<>();

        // Authentication endpoints
        endpoints.add(createEndpoint("POST", "/api/v1/auth/login", "Login and get authentication token"));

        // Posts endpoints
        endpoints.add(createEndpoint("GET", "/api/v1/posts", "Get all posts", 
            Map.of("queryParams", List.of("sort (latest|top|discussions)", "page", "limit", "tag", "author"))));
        endpoints.add(createEndpoint("GET", "/api/v1/posts/{slug}", "Get post by slug"));
        endpoints.add(createEndpoint("POST", "/api/v1/posts/{slug}/views", "Increment post views"));
        endpoints.add(createEndpoint("POST", "/api/v1/posts/{slug}/like", "Like a post"));
        endpoints.add(createEndpoint("DELETE", "/api/v1/posts/{slug}/like", "Unlike a post"));

        // Tags endpoints
        endpoints.add(createEndpoint("GET", "/api/v1/tags", "Get all tags"));
        endpoints.add(createEndpoint("GET", "/api/v1/tags/{slug}", "Get tag by slug"));

        // Authors endpoints
        endpoints.add(createEndpoint("GET", "/api/v1/authors/{idOrUsername}", "Get author by ID or username"));

        // Search endpoints
        endpoints.add(createEndpoint("GET", "/api/v1/search", "Search posts, authors, and tags",
            Map.of("queryParams", List.of("query (required)", "type", "limit"))));

        // Comments endpoints
        endpoints.add(createEndpoint("GET", "/api/v1/posts/{slug}/comments", "Get comments for a post"));
        endpoints.add(createEndpoint("POST", "/api/v1/posts/{slug}/comments", "Create a comment",
            Map.of("body", Map.of("content", "string", "parentId", "string (optional)"))));
        endpoints.add(createEndpoint("POST", "/api/v1/posts/{slug}/comments/{commentId}/like", "Like a comment"));

        apiInfo.put("endpoints", endpoints);

        return ResponseEntity.ok(apiInfo);
    }

    private Map<String, Object> createEndpoint(String method, String path, String description) {
        return createEndpoint(method, path, description, Collections.emptyMap());
    }

    private Map<String, Object> createEndpoint(String method, String path, String description, Map<String, Object> additionalInfo) {
        Map<String, Object> endpoint = new LinkedHashMap<>();
        endpoint.put("method", method);
        endpoint.put("path", path);
        endpoint.put("description", description);
        endpoint.putAll(additionalInfo);
        return endpoint;
    }
}

