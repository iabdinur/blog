package com.iabdinur.repository;

import com.iabdinur.dao.PostDao;
import com.iabdinur.model.Post;
import com.iabdinur.rowmapper.PostRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class PostJDBCDataAccessService implements PostDao {

    private final JdbcTemplate jdbcTemplate;
    private final PostRowMapper postRowMapper;

    public PostJDBCDataAccessService(JdbcTemplate jdbcTemplate,
                                      PostRowMapper postRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.postRowMapper = postRowMapper;
    }

    @Override
    public List<Post> selectAllPosts() {
        var sql = """
                SELECT id, title, slug, content, excerpt, cover_image, author_id, published_at,
                       scheduled_at, is_published, views, likes, comments_count, reading_time,
                       created_at, updated_at
                FROM posts
                ORDER BY created_at DESC
                LIMIT 1000
                """;
        return jdbcTemplate.query(sql, postRowMapper);
    }

    @Override
    public List<Post> selectPublishedPosts(int limit, int offset) {
        var sql = """
                SELECT id, title, slug, content, excerpt, cover_image, author_id, published_at,
                       scheduled_at, is_published, views, likes, comments_count, reading_time,
                       created_at, updated_at
                FROM posts
                WHERE is_published = true
                ORDER BY published_at DESC
                LIMIT ? OFFSET ?
                """;
        return jdbcTemplate.query(sql, postRowMapper, limit, offset);
    }

    @Override
    public List<Post> selectPostsByAuthorId(Long authorId, int limit, int offset) {
        var sql = """
                SELECT id, title, slug, content, excerpt, cover_image, author_id, published_at,
                       scheduled_at, is_published, views, likes, comments_count, reading_time,
                       created_at, updated_at
                FROM posts
                WHERE author_id = ? AND is_published = true
                ORDER BY published_at DESC
                LIMIT ? OFFSET ?
                """;
        return jdbcTemplate.query(sql, postRowMapper, authorId, limit, offset);
    }

    @Override
    public List<Post> selectPostsByTagSlug(String tagSlug, int limit, int offset) {
        var sql = """
                SELECT DISTINCT p.id, p.title, p.slug, p.content, p.excerpt, p.cover_image,
                       p.author_id, p.published_at, p.scheduled_at, p.is_published, p.views, p.likes,
                       p.comments_count, p.reading_time,
                       p.created_at, p.updated_at
                FROM posts p
                INNER JOIN post_tags pt ON p.id = pt.post_id
                INNER JOIN tags t ON pt.tag_id = t.id
                WHERE t.slug = ? AND p.is_published = true
                ORDER BY p.published_at DESC
                LIMIT ? OFFSET ?
                """;
        return jdbcTemplate.query(sql, postRowMapper, tagSlug, limit, offset);
    }

    @Override
    public Optional<Post> selectPostById(Long postId) {
        var sql = """
                SELECT id, title, slug, content, excerpt, cover_image, author_id, published_at,
                       scheduled_at, is_published, views, likes, comments_count, reading_time,
                       created_at, updated_at
                FROM posts
                WHERE id = ?
                """;
        return jdbcTemplate.query(sql, postRowMapper, postId)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Post> selectPostBySlug(String slug) {
        var sql = """
                SELECT id, title, slug, content, excerpt, cover_image, author_id, published_at,
                       scheduled_at, is_published, views, likes, comments_count, reading_time,
                       created_at, updated_at
                FROM posts
                WHERE slug = ?
                """;
        return jdbcTemplate.query(sql, postRowMapper, slug)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Post> selectPublishedPostBySlug(String slug) {
        var sql = """
                SELECT id, title, slug, content, excerpt, cover_image, author_id, published_at,
                       scheduled_at, is_published, views, likes, comments_count, reading_time,
                       created_at, updated_at
                FROM posts
                WHERE slug = ? AND is_published = true
                """;
        return jdbcTemplate.query(sql, postRowMapper, slug)
                .stream()
                .findFirst();
    }

    @Override
    public void insertPost(Post post) {
        var sql = """
                INSERT INTO posts(title, slug, content, excerpt, cover_image, author_id, published_at,
                                 scheduled_at, is_published, views, likes, comments_count, reading_time,
                                 created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getSlug());
            ps.setString(3, post.getContent());
            ps.setString(4, post.getExcerpt());
            ps.setString(5, post.getCoverImage());
            ps.setLong(6, post.getAuthor().getId());
            ps.setTimestamp(7, post.getPublishedAt() != null ? Timestamp.valueOf(post.getPublishedAt()) : null);
            ps.setTimestamp(8, post.getScheduledAt() != null ? Timestamp.valueOf(post.getScheduledAt()) : null);
            ps.setBoolean(9, post.getIsPublished() != null ? post.getIsPublished() : false);
            ps.setLong(10, post.getViews() != null ? post.getViews() : 0L);
            ps.setLong(11, post.getLikes() != null ? post.getLikes() : 0L);
            ps.setInt(12, post.getCommentsCount() != null ? post.getCommentsCount() : 0);
            ps.setObject(13, post.getReadingTime());
            ps.setTimestamp(14, Timestamp.valueOf(post.getCreatedAt()));
            ps.setTimestamp(15, Timestamp.valueOf(post.getUpdatedAt()));
            return ps;
        }, keyHolder);
        
        Long id = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
        post.setId(id);
    }

    @Override
    public boolean existsPostWithSlug(String slug) {
        var sql = """
                SELECT count(id)
                FROM posts
                WHERE slug = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, slug);
        return count != null && count > 0;
    }

    @Override
    public boolean existsPostById(Long postId) {
        var sql = """
                SELECT count(id)
                FROM posts
                WHERE id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, postId);
        return count != null && count > 0;
    }

    @Override
    public void deletePostById(Long postId) {
        var sql = """
                DELETE
                FROM posts
                WHERE id = ?
                """;
        jdbcTemplate.update(sql, postId);
    }

    @Override
    public void updatePost(Post update) {
        if (update.getTitle() != null) {
            String sql = "UPDATE posts SET title = ?, updated_at = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    update.getTitle(),
                    Timestamp.valueOf(LocalDateTime.now()),
                    update.getId());
        }
        if (update.getSlug() != null) {
            String sql = "UPDATE posts SET slug = ?, updated_at = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    update.getSlug(),
                    Timestamp.valueOf(LocalDateTime.now()),
                    update.getId());
        }
        if (update.getContent() != null) {
            String sql = "UPDATE posts SET content = ?, updated_at = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    update.getContent(),
                    Timestamp.valueOf(LocalDateTime.now()),
                    update.getId());
        }
        if (update.getExcerpt() != null) {
            String sql = "UPDATE posts SET excerpt = ?, updated_at = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    update.getExcerpt(),
                    Timestamp.valueOf(LocalDateTime.now()),
                    update.getId());
        }
        if (update.getIsPublished() != null) {
            String sql = "UPDATE posts SET is_published = ?, published_at = ?, updated_at = ? WHERE id = ?";
            LocalDateTime publishedAt = update.getIsPublished() && update.getPublishedAt() == null 
                    ? LocalDateTime.now() 
                    : update.getPublishedAt();
            jdbcTemplate.update(sql,
                    update.getIsPublished(),
                    publishedAt != null ? Timestamp.valueOf(publishedAt) : null,
                    Timestamp.valueOf(LocalDateTime.now()),
                    update.getId());
        }
        // Handle scheduled_at - check if it was explicitly set (not just null from not being updated)
        // We'll update it if the post object has scheduledAt set (even if null, to clear it)
        // This is a bit tricky - we need to check if scheduledAt field was modified
        // For now, we'll update it if isPublished is being set to true (to clear scheduled_at)
        if (update.getIsPublished() != null && update.getIsPublished()) {
            // Clear scheduled_at when manually publishing
            String sql = "UPDATE posts SET scheduled_at = NULL, updated_at = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    Timestamp.valueOf(LocalDateTime.now()),
                    update.getId());
        } else if (update.getScheduledAt() != null) {
            // Set scheduled_at
            String sql = "UPDATE posts SET scheduled_at = ?, updated_at = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    Timestamp.valueOf(update.getScheduledAt()),
                    Timestamp.valueOf(LocalDateTime.now()),
                    update.getId());
        }
        // Add more fields as needed
    }

    @Override
    public void incrementViews(Long postId) {
        var sql = """
                UPDATE posts
                SET views = views + 1
                WHERE id = ?
                """;
        int updated = jdbcTemplate.update(sql, postId);
        if (updated == 0) {
            throw new IllegalArgumentException("Post not found with id: " + postId);
        }
    }

    @Override
    public void incrementLikes(Long postId) {
        var sql = """
                UPDATE posts
                SET likes = likes + 1
                WHERE id = ?
                """;
        int updated = jdbcTemplate.update(sql, postId);
        if (updated == 0) {
            throw new IllegalArgumentException("Post not found with id: " + postId);
        }
    }

    @Override
    public void decrementLikes(Long postId) {
        var sql = """
                UPDATE posts
                SET likes = GREATEST(likes - 1, 0)
                WHERE id = ?
                """;
        int updated = jdbcTemplate.update(sql, postId);
        if (updated == 0) {
            throw new IllegalArgumentException("Post not found with id: " + postId);
        }
    }

    @Override
    public long countPublishedPosts() {
        var sql = """
                SELECT COUNT(id)
                FROM posts
                WHERE is_published = true
                """;
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }

    @Override
    public long countPostsByAuthorId(Long authorId) {
        var sql = """
                SELECT COUNT(id)
                FROM posts
                WHERE author_id = ? AND is_published = true
                """;
        Long count = jdbcTemplate.queryForObject(sql, Long.class, authorId);
        return count != null ? count : 0L;
    }

    @Override
    public long countPostsByTagSlug(String tagSlug) {
        var sql = """
                SELECT COUNT(DISTINCT p.id)
                FROM posts p
                INNER JOIN post_tags pt ON p.id = pt.post_id
                INNER JOIN tags t ON pt.tag_id = t.id
                WHERE t.slug = ? AND p.is_published = true
                """;
        Long count = jdbcTemplate.queryForObject(sql, Long.class, tagSlug);
        return count != null ? count : 0L;
    }

    @Override
    public List<Post> selectDraftsByAuthorId(Long authorId, int limit, int offset) {
        var sql = """
                SELECT id, title, slug, content, excerpt, cover_image, author_id, published_at,
                       scheduled_at, is_published, views, likes, comments_count, reading_time,
                       created_at, updated_at
                FROM posts
                WHERE author_id = ? AND is_published = false
                ORDER BY updated_at DESC
                LIMIT ? OFFSET ?
                """;
        return jdbcTemplate.query(sql, postRowMapper, authorId, limit, offset);
    }

    @Override
    public long countDraftsByAuthorId(Long authorId) {
        var sql = """
                SELECT COUNT(id)
                FROM posts
                WHERE author_id = ? AND is_published = false
                """;
        Long count = jdbcTemplate.queryForObject(sql, Long.class, authorId);
        return count != null ? count : 0L;
    }

    @Override
    public List<Post> selectScheduledPostsReadyToPublish() {
        var sql = """
                SELECT id, title, slug, content, excerpt, cover_image, author_id, published_at,
                       scheduled_at, is_published, views, likes, comments_count, reading_time,
                       created_at, updated_at
                FROM posts
                WHERE scheduled_at IS NOT NULL
                  AND is_published = false
                  AND scheduled_at <= ?
                ORDER BY scheduled_at ASC
                """;
        return jdbcTemplate.query(sql, postRowMapper, Timestamp.valueOf(LocalDateTime.now()));
    }

    @Override
    public void updatePostPublishedStatus(Long postId, boolean isPublished, LocalDateTime publishedAt) {
        var sql = """
                UPDATE posts
                SET is_published = ?, published_at = ?, scheduled_at = NULL, updated_at = ?
                WHERE id = ?
                """;
        jdbcTemplate.update(sql,
                isPublished,
                publishedAt != null ? Timestamp.valueOf(publishedAt) : null,
                Timestamp.valueOf(LocalDateTime.now()),
                postId);
    }
}
