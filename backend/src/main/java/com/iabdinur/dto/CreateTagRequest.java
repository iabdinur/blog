package com.iabdinur.dto;

public record CreateTagRequest(
    String name,
    String slug,
    String description
) {}

