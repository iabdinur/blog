package com.iabdinur.dto;

public record CreateAuthorRequest(
    String name,
    String username,
    String email,
    String bio,
    String avatar,
    String coverImage,
    String location,
    String website,
    String twitter,
    String github,
    String linkedin
) {}

