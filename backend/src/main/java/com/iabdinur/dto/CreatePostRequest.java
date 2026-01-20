package com.iabdinur.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreatePostRequest(
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    String title,
    
    @NotBlank(message = "Slug is required")
    @Size(min = 1, max = 200, message = "Slug must be between 1 and 200 characters")
    String slug,
    
    @NotBlank(message = "Content is required")
    @Size(min = 10, message = "Content must be at least 10 characters")
    String content,
    
    @Size(max = 500, message = "Excerpt must not exceed 500 characters")
    String excerpt,
    
    String coverImage,
    String contentImage,
    String authorId,
    List<String> tagIds,
    Boolean isPublished,
    Integer readingTime,
    String scheduledAt
) {}

