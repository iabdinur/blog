package com.iabdinur.controller;

import com.iabdinur.dto.CommentDTO;
import com.iabdinur.service.CommentService;
import com.iabdinur.service.UserService;
import com.iabdinur.service.AuthorService;
import com.iabdinur.util.JWTUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/posts/{slug}/comments")
public class CommentController {
    private final CommentService commentService;
    private final UserService userService;
    private final AuthorService authorService;
    private final JWTUtil jwtUtil;

    public CommentController(CommentService commentService,
                            UserService userService,
                            AuthorService authorService,
                            JWTUtil jwtUtil) {
        this.commentService = commentService;
        this.userService = userService;
        this.authorService = authorService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable String slug) {
        return ResponseEntity.ok(commentService.getCommentsByPostSlug(slug));
    }

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(
            @PathVariable String slug,
            @RequestBody CreateCommentRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // Extract user email from JWT token
        String userEmail = extractEmailFromToken(authHeader);
        if (userEmail == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required to post comments");
        }

        // Find or create Author based on User's email
        Long authorId = findOrCreateAuthorForUser(userEmail);
        
        Long parentId = request.parentId() != null && !request.parentId().isEmpty() 
            ? Long.parseLong(request.parentId()) 
            : null;
        
        CommentDTO comment = commentService.createComment(slug, request.content(), authorId, parentId);
        return ResponseEntity.ok(comment);
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<Void> likeComment(@PathVariable String commentId) {
        commentService.incrementLikes(Long.parseLong(commentId));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable String slug,
            @PathVariable String commentId,
            @Valid @RequestBody com.iabdinur.dto.UpdateCommentRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // Extract user email from JWT token
        String userEmail = extractEmailFromToken(authHeader);
        if (userEmail == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required to update comments");
        }

        // Find or create Author based on User's email
        Long authorId = findOrCreateAuthorForUser(userEmail);
        
        Optional<CommentDTO> updatedComment = commentService.updateComment(
            Long.parseLong(commentId), 
            request.content(), 
            authorId
        );
        
        return updatedComment
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable String slug,
            @PathVariable String commentId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // Extract user email from JWT token
        String userEmail = extractEmailFromToken(authHeader);
        if (userEmail == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required to delete comments");
        }

        // Find or create Author based on User's email
        Long authorId = findOrCreateAuthorForUser(userEmail);
        
        boolean deleted = commentService.deleteComment(Long.parseLong(commentId), authorId);
        
        return deleted 
            ? ResponseEntity.ok().build()
            : ResponseEntity.notFound().build();
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

    private Long findOrCreateAuthorForUser(String email) {
        // Get User info first to check for profile image
        var userOpt = userService.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        var user = userOpt.get();
        
        // First, try to find existing Author by email
        var authorOpt = authorService.getAuthorByEmail(email);
        if (authorOpt.isPresent()) {
            var author = authorOpt.get();
            // Update author avatar if user has profile image and author doesn't have one set
            if (user.profileImageId() != null && !user.profileImageId().isEmpty() 
                && (author.avatar() == null || author.avatar().isEmpty())) {
                // Update author avatar to point to user profile image
                // Store path with email - frontend will encode it when constructing the URL
                String avatarUrl = "/api/v1/users/" + email + "/profile-image";
                var updateRequest = new com.iabdinur.dto.CreateAuthorRequest(
                    author.name(),
                    author.username(),
                    author.email(),
                    author.bio(),
                    avatarUrl, // Set avatar to user profile image URL
                    author.coverImage(),
                    null, // location
                    null, // website
                    null, // github
                    null  // linkedin
                );
                authorService.updateAuthor(author.username(), updateRequest);
            }
            return Long.parseLong(author.id());
        }

        // If no Author exists, create Author from User info
        // Generate unique username from email (handle duplicates)
        String baseUsername = email.split("@")[0];
        String username = baseUsername;
        int counter = 1;
        while (authorService.getAuthorByUsername(username).isPresent()) {
            username = baseUsername + counter;
            counter++;
        }
        
        // Set avatar to user profile image URL if user has a profile image
        // Store path with email - frontend will encode it when constructing the URL
        String avatarUrl = null;
        if (user.profileImageId() != null && !user.profileImageId().isEmpty()) {
            avatarUrl = "/api/v1/users/" + email + "/profile-image";
        }
        
        // Create Author from User info
        var createAuthorRequest = new com.iabdinur.dto.CreateAuthorRequest(
            user.name(),
            username,
            email,
            "", // bio
            avatarUrl, // avatar - set to user profile image URL if available
            null, // coverImage
            null, // location
            null, // website
            null, // github
            null  // linkedin
        );

        var author = authorService.createAuthor(createAuthorRequest);
        return Long.parseLong(author.id());
    }

    public record CreateCommentRequest(String content, String parentId) {}
}

