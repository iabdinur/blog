package com.iabdinur.dto;

import java.util.List;

public record PostListResponse(
    List<PostDTO> posts,
    Integer total,
    Integer page,
    Integer limit
) {}

