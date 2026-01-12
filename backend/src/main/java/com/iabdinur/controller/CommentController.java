package com.iabdinur.controller;

import com.iabdinur.dto.CommentDTO;
import com.iabdinur.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts/{slug}/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable String slug) {
        return ResponseEntity.ok(commentService.getCommentsByPostSlug(slug));
    }

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(
            @PathVariable String slug,
            @RequestBody CreateCommentRequest request) {
        // TODO: Get authorId from authentication context
        Long authorId = 1L; // Placeholder
        Long parentId = request.parentId() != null ? Long.parseLong(request.parentId()) : null;
        
        CommentDTO comment = commentService.createComment(slug, request.content(), authorId, parentId);
        return ResponseEntity.ok(comment);
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<Void> likeComment(@PathVariable String commentId) {
        commentService.incrementLikes(Long.parseLong(commentId));
        return ResponseEntity.ok().build();
    }

    public record CreateCommentRequest(String content, String parentId) {}
}

