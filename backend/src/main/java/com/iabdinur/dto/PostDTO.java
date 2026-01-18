package com.iabdinur.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PostDTO(
    String id,
    String title,
    String slug,
    String content,
    String excerpt,
    String coverImage,
    String publishedAt,
    String scheduledAt,
    String updatedAt,
    AuthorDTO author,
    List<TagDTO> tags,
    Integer readingTime,
    Long views,
    Long likes,
    Integer commentsCount,
    Boolean isPublished
) {}

