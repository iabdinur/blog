package com.iabdinur.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record AuthorDTO(
    String id,
    String name,
    String username,
    String email,
    String bio,
    String avatar,
    String coverImage,
    String location,
    String website,
    Map<String, String> socialLinks,
    Integer followersCount,
    Integer followingCount,
    Integer postsCount,
    String joinedAt
) {
    public static AuthorDTO fromEntity(com.iabdinur.model.Author author) {
        Map<String, String> socialLinks = Map.of(
            "twitter", author.getTwitter() != null ? author.getTwitter() : "",
            "github", author.getGithub() != null ? author.getGithub() : "",
            "linkedin", author.getLinkedin() != null ? author.getLinkedin() : ""
        );

        return new AuthorDTO(
            author.getId().toString(),
            author.getName(),
            author.getUsername(),
            author.getEmail(),
            author.getBio(),
            author.getAvatar(),
            author.getCoverImage(),
            author.getLocation(),
            author.getWebsite(),
            socialLinks,
            author.getFollowersCount(),
            author.getFollowingCount(),
            author.getPostsCount(),
            author.getJoinedAt() != null ? author.getJoinedAt().toString() : null
        );
    }
}

