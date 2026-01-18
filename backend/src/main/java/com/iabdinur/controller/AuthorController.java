package com.iabdinur.controller;

import com.iabdinur.dto.AuthorDTO;
import com.iabdinur.service.AuthorService;
import com.iabdinur.util.JWTUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/authors")
public class AuthorController {
    private final AuthorService authorService;
    private final JWTUtil jwtUtil;

    public AuthorController(AuthorService authorService, JWTUtil jwtUtil) {
        this.authorService = authorService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<java.util.List<AuthorDTO>> getAllAuthors() {
        return ResponseEntity.ok(authorService.getAllAuthors());
    }

    @GetMapping("/{idOrUsername}")
    public ResponseEntity<AuthorDTO> getAuthor(@PathVariable String idOrUsername) {
        // Try as username first, then as ID
        return authorService.getAuthorByUsername(idOrUsername)
            .map(ResponseEntity::ok)
            .orElseGet(() -> authorService.getAuthorById(idOrUsername)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()));
    }

    @PostMapping
    public ResponseEntity<?> createAuthor(@RequestBody com.iabdinur.dto.CreateAuthorRequest request) {
        // Validate required fields
        if (request.name() == null || request.name().trim().isEmpty()) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST)
                .body("Name is required");
        }
        if (request.email() == null || request.email().trim().isEmpty()) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST)
                .body("Email is required");
        }
        if (request.username() == null || request.username().trim().isEmpty()) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST)
                .body("Username is required");
        }
        
        try {
            AuthorDTO createdAuthor = authorService.createAuthor(request);
            return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(createdAuthor);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // Handle unique constraint violations (duplicate username or email)
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("username")) {
                return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST)
                    .body("Username already exists");
            } else if (errorMessage != null && errorMessage.contains("email")) {
                return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST)
                    .body("Email already exists");
            }
            return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST)
                .body("Failed to create author: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST)
                .body("Failed to create author: " + e.getMessage());
        }
    }

    @PutMapping("/{username}")
    public ResponseEntity<AuthorDTO> updateAuthor(@PathVariable String username, @RequestBody com.iabdinur.dto.CreateAuthorRequest request) {
        return authorService.updateAuthor(username, request)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable String username) {
        boolean deleted = authorService.deleteAuthor(username);
        return deleted 
            ? ResponseEntity.ok().build()
            : ResponseEntity.notFound().build();
    }

}

