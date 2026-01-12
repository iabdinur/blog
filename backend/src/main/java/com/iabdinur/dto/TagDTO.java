package com.iabdinur.dto;

public record TagDTO(
    String id,
    String name,
    String slug,
    String description,
    Integer postsCount
) {
    public static TagDTO fromEntity(com.iabdinur.model.Tag tag) {
        return new TagDTO(
            tag.getId().toString(),
            tag.getName(),
            tag.getSlug(),
            tag.getDescription(),
            tag.getPostsCount()
        );
    }
}

