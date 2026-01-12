package com.iabdinur.controller;

import com.iabdinur.dto.SearchResponse;
import com.iabdinur.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<SearchResponse> search(
            @RequestParam String query,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer limit) {
        SearchResponse response = searchService.search(query, type, limit);
        return ResponseEntity.ok(response);
    }
}

