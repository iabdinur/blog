package com.iabdinur.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "Author")
@Table(name = "authors", uniqueConstraints = {
    @UniqueConstraint(name = "authors_username_unique", columnNames = "username"),
    @UniqueConstraint(name = "authors_email_unique", columnNames = "email")
})
public class Author {

    @Id
    @SequenceGenerator(name = "authors_id_seq", sequenceName = "authors_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "authors_id_seq")
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(name = "username", nullable = false, unique = true, columnDefinition = "TEXT")
    private String username;

    @Column(name = "email", nullable = false, unique = true, columnDefinition = "TEXT")
    private String email;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "avatar", columnDefinition = "TEXT")
    private String avatar;

    @Column(name = "cover_image", columnDefinition = "TEXT")
    private String coverImage;

    @Column(name = "location", columnDefinition = "TEXT")
    private String location;

    @Column(name = "website", columnDefinition = "TEXT")
    private String website;

    @Column(name = "twitter", columnDefinition = "TEXT")
    private String twitter;

    @Column(name = "github", columnDefinition = "TEXT")
    private String github;

    @Column(name = "linkedin", columnDefinition = "TEXT")
    private String linkedin;

    @Column(name = "followers_count", nullable = false)
    private Integer followersCount;

    @Column(name = "following_count", nullable = false)
    private Integer followingCount;

    @Column(name = "posts_count", nullable = false)
    private Integer postsCount;

    @Column(name = "joined_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime joinedAt;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime updatedAt;

    // Constructors
    public Author() {
    }

    public Author(String name, String username, String email) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.followersCount = 0;
        this.followingCount = 0;
        this.postsCount = 0;
        this.joinedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Author(Long id, String name, String username, String email, LocalDateTime joinedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.followersCount = 0;
        this.followingCount = 0;
        this.postsCount = 0;
        this.joinedAt = joinedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public Integer getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(Integer followersCount) {
        this.followersCount = followersCount;
    }

    public Integer getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(Integer followingCount) {
        this.followingCount = followingCount;
    }

    public Integer getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(Integer postsCount) {
        this.postsCount = postsCount;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", bio='" + bio + '\'' +
                ", avatar='" + avatar + '\'' +
                ", coverImage='" + coverImage + '\'' +
                ", location='" + location + '\'' +
                ", website='" + website + '\'' +
                ", twitter='" + twitter + '\'' +
                ", github='" + github + '\'' +
                ", linkedin='" + linkedin + '\'' +
                ", followersCount=" + followersCount +
                ", followingCount=" + followingCount +
                ", postsCount=" + postsCount +
                ", joinedAt=" + joinedAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return Objects.equals(id, author.id) &&
                Objects.equals(username, author.username) &&
                Objects.equals(email, author.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email);
    }
}
