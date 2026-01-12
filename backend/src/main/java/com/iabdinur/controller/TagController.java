package com.iabdinur.controller;

import com.iabdinur.dto.TagDTO;
import com.iabdinur.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
public class TagController {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ResponseEntity<List<TagDTO>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<TagDTO> getTagBySlug(@PathVariable String slug) {
        return tagService.getTagBySlug(slug)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TagDTO> createTag(@RequestBody com.iabdinur.dto.CreateTagRequest request) {
        try {
            TagDTO createdTag = tagService.createTag(request);
            return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(createdTag);
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{slug}")
    public ResponseEntity<TagDTO> updateTag(@PathVariable String slug, @RequestBody com.iabdinur.dto.CreateTagRequest request) {
        return tagService.updateTag(slug, request)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{slug}")
    public ResponseEntity<Void> deleteTag(@PathVariable String slug) {
        boolean deleted = tagService.deleteTag(slug);
        return deleted 
            ? ResponseEntity.ok().build()
            : ResponseEntity.notFound().build();
    }
}

