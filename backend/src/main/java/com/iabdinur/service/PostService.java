package com.iabdinur.service;

import com.iabdinur.dao.AuthorDao;
import com.iabdinur.dao.PostDao;
import com.iabdinur.dao.TagDao;
import com.iabdinur.dto.*;
import com.iabdinur.model.Author;
import com.iabdinur.model.Post;
import com.iabdinur.model.Tag;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PostService {
    private final PostDao postDao;
    private final AuthorDao authorDao;
    private final TagDao tagDao;
    private final JdbcTemplate jdbcTemplate;

    public PostService(PostDao postDao,
                      AuthorDao authorDao,
                      TagDao tagDao,
                      JdbcTemplate jdbcTemplate) {
        this.postDao = postDao;
        this.authorDao = authorDao;
        this.tagDao = tagDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public PostListResponse getAllPosts(String sort, Integer page, Integer limit, String tag, String author) {
        int offset = (page - 1) * limit;
        List<Post> posts;
        long total;

        if (tag != null) {
            posts = postDao.selectPostsByTagSlug(tag, limit, offset);
            total = postDao.countPostsByTagSlug(tag);
        } else if (author != null) {
            Optional<Author> authorOpt = authorDao.selectAuthorByUsername(author);
            if (authorOpt.isEmpty()) {
                return new PostListResponse(new ArrayList<>(), 0, page, limit);
            }
            posts = postDao.selectPostsByAuthorId(authorOpt.get().getId(), limit, offset);
            total = postDao.countPostsByAuthorId(authorOpt.get().getId());
        } else {
            // Handle sorting manually
            posts = postDao.selectPublishedPosts(limit, offset);
            total = postDao.countPublishedPosts();
            
            // Apply sorting
            if ("top".equals(sort)) {
                posts.sort((a, b) -> Long.compare(b.getLikes(), a.getLikes()));
            } else if ("discussions".equals(sort)) {
                posts.sort((a, b) -> Integer.compare(b.getCommentsCount(), a.getCommentsCount()));
            }
            // "latest" is default - already sorted by published_at DESC in query
        }

        // Load relationships
        loadPostRelationships(posts);

        List<PostDTO> postDTOs = posts.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());

        return new PostListResponse(postDTOs, (int) total, page, limit);
    }

    @Transactional(readOnly = true)
    public Optional<PostDTO> getPostBySlug(String slug) {
        Optional<Post> postOpt = postDao.selectPublishedPostBySlug(slug);
        if (postOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Post post = postOpt.get();
        loadPostRelationships(List.of(post));
        return Optional.of(convertToDTO(post));
    }

    @Transactional(readOnly = true)
    public PostListResponse searchPosts(String query, Integer page, Integer limit) {
        int offset = (page - 1) * limit;
        
        var sql = """
                SELECT DISTINCT p.id, p.title, p.slug, p.content, p.excerpt, p.cover_image, p.author_id,
                       p.published_at, p.is_published, p.views, p.likes, p.comments_count, p.reading_time,
                       p.created_at, p.updated_at
                FROM posts p
                WHERE p.is_published = true
                  AND (LOWER(p.title) LIKE LOWER(CONCAT('%', ?, '%'))
                   OR LOWER(p.excerpt) LIKE LOWER(CONCAT('%', ?, '%'))
                   OR LOWER(p.content) LIKE LOWER(CONCAT('%', ?, '%')))
                ORDER BY p.published_at DESC
                LIMIT ? OFFSET ?
                """;
        
        List<Post> posts = jdbcTemplate.query(sql,
            (rs, rowNum) -> {
                Post post = new Post();
                post.setId(rs.getLong("id"));
                post.setTitle(rs.getString("title"));
                post.setSlug(rs.getString("slug"));
                post.setContent(rs.getString("content"));
                post.setExcerpt(rs.getString("excerpt"));
                post.setCoverImage(rs.getString("cover_image"));
                if (rs.getTimestamp("published_at") != null) {
                    post.setPublishedAt(rs.getTimestamp("published_at").toLocalDateTime());
                }
                post.setIsPublished(rs.getBoolean("is_published"));
                post.setViews(rs.getLong("views"));
                post.setLikes(rs.getLong("likes"));
                post.setCommentsCount(rs.getInt("comments_count"));
                int readingTime = rs.getInt("reading_time");
                if (!rs.wasNull()) {
                    post.setReadingTime(readingTime);
                }
                post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                post.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                return post;
            },
            query, query, query, limit, offset);
        
        long total = jdbcTemplate.queryForObject(
            "SELECT COUNT(DISTINCT p.id) FROM posts p WHERE p.is_published = true AND " +
            "(LOWER(p.title) LIKE LOWER(CONCAT('%', ?, '%')) OR " +
            "LOWER(p.excerpt) LIKE LOWER(CONCAT('%', ?, '%')) OR " +
            "LOWER(p.content) LIKE LOWER(CONCAT('%', ?, '%')))",
            Long.class, query, query, query);
        
        // Load relationships
        loadPostRelationships(posts);

        List<PostDTO> postDTOs = posts.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());

        return new PostListResponse(postDTOs, (int) total, page, limit);
    }

    @Transactional
    public void incrementViews(String slug) {
        Optional<Post> postOpt = postDao.selectPostBySlug(slug);
        if (postOpt.isEmpty()) {
            throw new IllegalArgumentException("Post not found with slug: " + slug);
        }
        postDao.incrementViews(postOpt.get().getId());
    }

    @Transactional
    public void incrementLikes(String slug) {
        Optional<Post> postOpt = postDao.selectPostBySlug(slug);
        if (postOpt.isEmpty()) {
            throw new IllegalArgumentException("Post not found with slug: " + slug);
        }
        postDao.incrementLikes(postOpt.get().getId());
    }

    @Transactional
    public void decrementLikes(String slug) {
        Optional<Post> postOpt = postDao.selectPostBySlug(slug);
        if (postOpt.isEmpty()) {
            throw new IllegalArgumentException("Post not found with slug: " + slug);
        }
        postDao.decrementLikes(postOpt.get().getId());
    }

    @Transactional
    public PostDTO createPost(CreatePostRequest request) {
        // Find author
        Optional<Author> authorOpt = authorDao.selectAuthorById(Long.parseLong(request.authorId()));
        if (authorOpt.isEmpty()) {
            throw new IllegalArgumentException("Author not found with id: " + request.authorId());
        }
        Author author = authorOpt.get();

        // Create post
        Post post = new Post();
        post.setTitle(request.title());
        post.setSlug(request.slug());
        post.setContent(request.content());
        post.setExcerpt(request.excerpt());
        post.setCoverImage(request.coverImage());
        post.setAuthor(author);
        post.setIsPublished(request.isPublished() != null ? request.isPublished() : false);
        post.setViews(0L);
        post.setLikes(0L);
        post.setCommentsCount(0);
        post.setReadingTime(request.readingTime());
        post.setCreatedAt(java.time.LocalDateTime.now());
        post.setUpdatedAt(java.time.LocalDateTime.now());
        
        if (post.getIsPublished()) {
            post.setPublishedAt(java.time.LocalDateTime.now());
        }

        postDao.insertPost(post);

        // Handle tags separately (many-to-many relationship)
        if (request.tagIds() != null && !request.tagIds().isEmpty()) {
            for (String tagId : request.tagIds()) {
                Optional<Tag> tagOpt = tagDao.selectTagById(Long.parseLong(tagId));
                if (tagOpt.isPresent()) {
                    // Insert into post_tags junction table
                    jdbcTemplate.update(
                        "INSERT INTO post_tags(post_id, tag_id) VALUES (?, ?)",
                        post.getId(), tagOpt.get().getId());
                }
            }
        }

        // Load relationships for DTO conversion
        loadPostRelationships(List.of(post));
        return convertToDTO(post);
    }

    @Transactional
    public Optional<PostDTO> updatePost(String slug, CreatePostRequest request) {
        Optional<Post> postOpt = postDao.selectPostBySlug(slug);
        if (postOpt.isEmpty()) {
            return Optional.empty();
        }

        Post post = postOpt.get();
        boolean wasPublished = post.getIsPublished();
        
        post.setTitle(request.title());
        post.setSlug(request.slug());
        post.setContent(request.content());
        post.setExcerpt(request.excerpt());
        post.setCoverImage(request.coverImage());
        post.setReadingTime(request.readingTime());
        post.setUpdatedAt(java.time.LocalDateTime.now());

        // Update author if changed
        if (request.authorId() != null) {
            Optional<Author> authorOpt = authorDao.selectAuthorById(Long.parseLong(request.authorId()));
            authorOpt.ifPresent(post::setAuthor);
        }

        // Update published status
        if (request.isPublished() != null) {
            post.setIsPublished(request.isPublished());
            if (request.isPublished() && !wasPublished) {
                post.setPublishedAt(java.time.LocalDateTime.now());
            }
        }

        postDao.updatePost(post);

        // Update tags
        if (request.tagIds() != null) {
            // Delete existing tags
            jdbcTemplate.update("DELETE FROM post_tags WHERE post_id = ?", post.getId());
            
            // Insert new tags
            for (String tagId : request.tagIds()) {
                Optional<Tag> tagOpt = tagDao.selectTagById(Long.parseLong(tagId));
                if (tagOpt.isPresent()) {
                    jdbcTemplate.update(
                        "INSERT INTO post_tags(post_id, tag_id) VALUES (?, ?)",
                        post.getId(), tagOpt.get().getId());
                }
            }
        }

        // Load relationships for DTO conversion
        loadPostRelationships(List.of(post));
        return Optional.of(convertToDTO(post));
    }

    @Transactional
    public boolean deletePost(String slug) {
        Optional<Post> postOpt = postDao.selectPostBySlug(slug);
        if (postOpt.isEmpty()) {
            return false;
        }
        postDao.deletePostById(postOpt.get().getId());
        return true;
    }

    @Transactional(readOnly = true)
    public Optional<PostDTO> getPostBySlugForAdmin(String slug) {
        Optional<Post> postOpt = postDao.selectPostBySlug(slug);
        if (postOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Post post = postOpt.get();
        loadPostRelationships(List.of(post));
        return Optional.of(convertToDTO(post));
    }

    private void loadPostRelationships(List<Post> posts) {
        for (Post post : posts) {
            // Load author
            if (post.getId() != null) {
                var authorSql = "SELECT author_id FROM posts WHERE id = ?";
                Long authorId = jdbcTemplate.queryForObject(authorSql, Long.class, post.getId());
                if (authorId != null) {
                    authorDao.selectAuthorById(authorId).ifPresent(post::setAuthor);
                }
                
                // Load tags
                var tagsSql = """
                    SELECT t.id, t.name, t.slug, t.description, t.posts_count, t.created_at, t.updated_at
                    FROM tags t
                    INNER JOIN post_tags pt ON t.id = pt.tag_id
                    WHERE pt.post_id = ?
                    """;
                List<Tag> tags = jdbcTemplate.query(tagsSql,
                    (rs, rowNum) -> {
                        Tag tag = new Tag(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("slug"),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getTimestamp("updated_at").toLocalDateTime()
                        );
                        tag.setDescription(rs.getString("description"));
                        tag.setPostsCount(rs.getInt("posts_count"));
                        return tag;
                    },
                    post.getId());
                post.setTags(new HashSet<>(tags));
            }
        }
    }

    private PostDTO convertToDTO(Post post) {
        AuthorDTO authorDTO = post.getAuthor() != null 
            ? AuthorDTO.fromEntity(post.getAuthor())
            : null;

        List<TagDTO> tagDTOs = (post.getTags() != null ? post.getTags() : new HashSet<Tag>()).stream()
            .map(TagDTO::fromEntity)
            .collect(Collectors.toList());

        String slug = post.getSlug();
        
        return new PostDTO(
            post.getId().toString(),
            post.getTitle(),
            slug,
            post.getContent(),
            post.getExcerpt(),
            post.getCoverImage(),
            post.getPublishedAt() != null ? post.getPublishedAt().toString() : null,
            post.getUpdatedAt() != null ? post.getUpdatedAt().toString() : null,
            authorDTO,
            tagDTOs,
            post.getReadingTime(),
            post.getViews(),
            post.getLikes(),
            post.getCommentsCount(),
            post.getIsPublished()
        );
    }
}
