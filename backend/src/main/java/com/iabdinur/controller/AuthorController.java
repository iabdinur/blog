package com.iabdinur.controller;

import com.iabdinur.dto.AuthorDTO;
import com.iabdinur.service.AuthorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/authors")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
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
    public ResponseEntity<AuthorDTO> createAuthor(@RequestBody com.iabdinur.dto.CreateAuthorRequest request) {
        try {
            AuthorDTO createdAuthor = authorService.createAuthor(request);
            return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(createdAuthor);
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST).build();
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

