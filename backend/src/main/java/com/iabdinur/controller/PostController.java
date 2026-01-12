package com.iabdinur.controller;

import com.iabdinur.dto.PostDTO;
import com.iabdinur.dto.PostListResponse;
import com.iabdinur.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
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
    public ResponseEntity<PostDTO> createPost(@RequestBody com.iabdinur.dto.CreatePostRequest request) {
        try {
            PostDTO createdPost = postService.createPost(request);
            return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(createdPost);
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{slug}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable String slug, @RequestBody com.iabdinur.dto.CreatePostRequest request) {
        return postService.updatePost(slug, request)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
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
}

