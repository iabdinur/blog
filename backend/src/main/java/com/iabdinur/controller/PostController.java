package com.iabdinur.controller;

import com.iabdinur.dto.PostDTO;
import com.iabdinur.dto.PostListResponse;
import com.iabdinur.service.AuthorService;
import com.iabdinur.service.PostService;
import com.iabdinur.service.UserService;
import com.iabdinur.util.JWTUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;
    private final AuthorService authorService;
    private final UserService userService;
    private final JWTUtil jwtUtil;

    public PostController(PostService postService, AuthorService authorService, UserService userService, JWTUtil jwtUtil) {
        this.postService = postService;
        this.authorService = authorService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<PostListResponse> getAllPosts(
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String author) {
        PostListResponse response = postService.getAllPosts(sort, page, limit, tag, author);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<PostDTO> getPostBySlug(@PathVariable String slug) {
        return postService.getPostBySlug(slug)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{slug}/views")
    public ResponseEntity<Void> incrementViews(@PathVariable String slug) {
        postService.incrementViews(slug);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{slug}/like")
    public ResponseEntity<Void> likePost(@PathVariable String slug) {
        postService.incrementLikes(slug);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{slug}/like")
    public ResponseEntity<Void> unlikePost(@PathVariable String slug) {
        postService.decrementLikes(slug);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<PostDTO> createPost(@Valid @RequestBody com.iabdinur.dto.CreatePostRequest request) {
        try {
            PostDTO createdPost = postService.createPost(request);
            return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(createdPost);
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{slug}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable String slug, @Valid @RequestBody com.iabdinur.dto.CreatePostRequest request) {
        try {
            return postService.updatePost(slug, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to update post: " + e.getMessage());
        }
    }

    @DeleteMapping("/{slug}")
    public ResponseEntity<Void> deletePost(@PathVariable String slug) {
        boolean deleted = postService.deletePost(slug);
        return deleted 
            ? ResponseEntity.ok().build()
            : ResponseEntity.notFound().build();
    }

    @GetMapping("/{slug}/admin")
    public ResponseEntity<PostDTO> getPostBySlugForAdmin(@PathVariable String slug) {
        return postService.getPostBySlugForAdmin(slug)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/drafts")
    public ResponseEntity<PostListResponse> getDrafts(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        
        // Extract user email from JWT token
        String userEmail = extractEmailFromToken(authHeader);
        if (userEmail == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        // Get author ID from email
        Long authorId = getAuthorIdFromEmail(userEmail);
        if (authorId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Author profile not found");
        }

        PostListResponse drafts = postService.getDraftsByAuthor(authorId, page, limit);
        return ResponseEntity.ok(drafts);
    }

    @PostMapping("/{slug}/publish")
    public ResponseEntity<PostDTO> publishDraft(
            @PathVariable String slug,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // Extract user email from JWT token
        String userEmail = extractEmailFromToken(authHeader);
        if (userEmail == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        // Verify the post belongs to the author
        Long authorId = getAuthorIdFromEmail(userEmail);
        if (authorId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Author profile not found");
        }

        // Get post and verify ownership
        var postOpt = postService.getPostBySlugForAdmin(slug);
        if (postOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var post = postOpt.get();
        if (!post.author().id().equals(authorId.toString())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only publish your own posts");
        }

        return postService.publishDraft(slug)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    private String extractEmailFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        
        try {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            return jwtUtil.getSubject(token); // JWT subject is the email
        } catch (Exception e) {
            return null;
        }
    }

    private Long getAuthorIdFromEmail(String email) {
        return authorService.getAuthorByEmail(email)
            .map(author -> Long.parseLong(author.id()))
            .orElse(null);
    }
}

