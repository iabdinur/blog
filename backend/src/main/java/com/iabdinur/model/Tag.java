package com.iabdinur.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "Tag")
@Table(name = "tags", uniqueConstraints = {
    @UniqueConstraint(name = "tags_name_unique", columnNames = "name"),
    @UniqueConstraint(name = "tags_slug_unique", columnNames = "slug")
})
public class Tag {

    @Id
    @SequenceGenerator(name = "tags_id_seq", sequenceName = "tags_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tags_id_seq")
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, columnDefinition = "TEXT")
    private String name;

    @Column(name = "slug", nullable = false, unique = true, columnDefinition = "TEXT")
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "posts_count", nullable = false)
    private Integer postsCount;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime updatedAt;

    // Constructors
    public Tag() {
    }

    public Tag(String name, String slug) {
        this.name = name;
        this.slug = slug;
        this.postsCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Tag(Long id, String name, String slug, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.postsCount = 0;
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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(Integer postsCount) {
        this.postsCount = postsCount;
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
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", description='" + description + '\'' +
                ", postsCount=" + postsCount +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(id, tag.id) &&
                Objects.equals(slug, tag.slug);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, slug);
    }
}
