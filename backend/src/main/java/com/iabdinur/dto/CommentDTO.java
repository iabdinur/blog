package com.iabdinur.dto;

import java.util.List;

public record CommentDTO(
    String id,
    String content,
    AuthorDTO author,
    String postId,
    String parentId,
    List<CommentDTO> replies,
    Integer likes,
    String createdAt
) {}

