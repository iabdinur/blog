package com.iabdinur.dto;

import java.util.List;

public record SearchResponse(
    List<PostDTO> posts,
    List<AuthorDTO> authors,
    List<TagDTO> tags,
    Integer total
) {}

