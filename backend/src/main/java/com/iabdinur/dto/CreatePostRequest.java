package com.iabdinur.dto;

import java.util.List;

public record CreatePostRequest(
    String title,
    String slug,
    String content,
    String excerpt,
    String coverImage,
    String authorId,
    List<String> tagIds,
    Boolean isPublished,
    Integer readingTime
) {}

