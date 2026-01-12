package com.iabdinur.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(name = "Post")
@Table(name = "posts", uniqueConstraints = @UniqueConstraint(name = "posts_slug_unique", columnNames = "slug"))
public class Post {

    @Id
    @SequenceGenerator(name = "posts_id_seq", sequenceName = "posts_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "posts_id_seq")
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(name = "slug", nullable = false, unique = true, columnDefinition = "TEXT")
    private String slug;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "excerpt", columnDefinition = "TEXT")
    private String excerpt;

    @Column(name = "cover_image", columnDefinition = "TEXT")
    private String coverImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "posts_author_id_fkey"))
    private Author author;

    @Column(name = "published_at", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime publishedAt;

    @Column(name = "is_published", nullable = false)
    private Boolean isPublished;

    @Column(name = "views", nullable = false)
    private Long views;

    @Column(name = "likes", nullable = false)
    private Long likes;

    @Column(name = "comments_count", nullable = false)
    private Integer commentsCount;

    @Column(name = "reading_time")
    private Integer readingTime;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "post_tags",
        joinColumns = @JoinColumn(name = "post_id", foreignKey = @ForeignKey(name = "post_tags_post_id_fkey")),
        inverseJoinColumns = @JoinColumn(name = "tag_id", foreignKey = @ForeignKey(name = "post_tags_tag_id_fkey"))
    )
    private Set<Tag> tags = new HashSet<>();

    // Constructors
    public Post() {
    }

    public Post(String title, String slug, String content, Author author) {
        this.title = title;
        this.slug = slug;
        this.content = content;
        this.author = author;
        this.isPublished = false;
        this.views = 0L;
        this.likes = 0L;
        this.commentsCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Post(Long id, String title, String slug, String content, Author author, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.content = content;
        this.author = author;
        this.isPublished = false;
        this.views = 0L;
        this.likes = 0L;
        this.commentsCount = 0;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public Integer getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Integer commentsCount) {
        this.commentsCount = commentsCount;
    }

    public Integer getReadingTime() {
        return readingTime;
    }

    public void setReadingTime(Integer readingTime) {
        this.readingTime = readingTime;
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
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", slug='" + slug + '\'' +
                ", content='" + content + '\'' +
                ", excerpt='" + excerpt + '\'' +
                ", coverImage='" + coverImage + '\'' +
                ", author=" + (author != null ? author.getId() : null) +
                ", publishedAt=" + publishedAt +
                ", isPublished=" + isPublished +
                ", views=" + views +
                ", likes=" + likes +
                ", commentsCount=" + commentsCount +
                ", readingTime=" + readingTime +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id) &&
                Objects.equals(slug, post.slug);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, slug);
    }
}
