package com.iabdinur.service;

import com.iabdinur.dao.AuthorDao;
import com.iabdinur.dao.CommentDao;
import com.iabdinur.dao.PostDao;
import com.iabdinur.dto.AuthorDTO;
import com.iabdinur.dto.CommentDTO;
import com.iabdinur.model.Author;
import com.iabdinur.model.Comment;
import com.iabdinur.model.Post;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {
    private final CommentDao commentDao;
    private final AuthorDao authorDao;
    private final PostDao postDao;
    private final JdbcTemplate jdbcTemplate;

    public CommentService(CommentDao commentDao,
                         AuthorDao authorDao,
                         PostDao postDao,
                         JdbcTemplate jdbcTemplate) {
        this.commentDao = commentDao;
        this.authorDao = authorDao;
        this.postDao = postDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CommentDTO> getCommentsByPostSlug(String slug) {
        Optional<Post> postOpt = findPostBySlug(slug);
        if (postOpt.isEmpty()) {
            return new ArrayList<>();
        }

        List<Comment> allComments = commentDao.selectCommentsByPostId(postOpt.get().getId());
        
        // Load relationships
        loadCommentRelationships(allComments);
        
        // Build comment tree (top-level comments with replies)
        List<Comment> topLevelComments = allComments.stream()
            .filter(comment -> comment.getParent() == null)
            .collect(Collectors.toList());
        
        // Build reply structure
        for (Comment comment : allComments) {
            if (comment.getParent() != null) {
                Comment parent = allComments.stream()
                    .filter(c -> c.getId().equals(comment.getParent().getId()))
                    .findFirst()
                    .orElse(null);
                if (parent != null) {
                    parent.getReplies().add(comment);
                }
            }
        }
        
        return topLevelComments.stream()
            .map(comment -> convertToDTO(comment, true))
            .collect(Collectors.toList());
    }

    @Transactional
    public CommentDTO createComment(String slug, String content, Long authorId, Long parentId) {
        Optional<Post> postOpt = findPostBySlug(slug);
        if (postOpt.isEmpty()) {
            throw new RuntimeException("Post not found");
        }

        Optional<Author> authorOpt = authorDao.selectAuthorById(authorId);
        if (authorOpt.isEmpty()) {
            throw new RuntimeException("Author not found");
        }

        Comment comment = new Comment();
        comment.setPost(postOpt.get());
        comment.setAuthor(authorOpt.get());
        comment.setContent(content);
        comment.setLikes(0);
        comment.setCreatedAt(java.time.LocalDateTime.now());
        comment.setUpdatedAt(java.time.LocalDateTime.now());
        
        if (parentId != null) {
            Optional<Comment> parentOpt = commentDao.selectCommentById(parentId);
            parentOpt.ifPresent(comment::setParent);
        }

        commentDao.insertComment(comment);
        
        // Increment comments count on post
        jdbcTemplate.update(
            "UPDATE posts SET comments_count = comments_count + 1 WHERE id = ?",
            postOpt.get().getId());

        // Load relationships for DTO
        loadCommentRelationships(List.of(comment));
        return convertToDTO(comment, false);
    }

    @Transactional
    public void incrementLikes(Long commentId) {
        if (!commentDao.existsCommentById(commentId)) {
            throw new IllegalArgumentException("Comment not found with id: " + commentId);
        }
        commentDao.incrementLikes(commentId);
    }

    @Transactional
    public Optional<CommentDTO> updateComment(Long commentId, String content, Long authorId) {
        Optional<Comment> commentOpt = commentDao.selectCommentById(commentId);
        if (commentOpt.isEmpty()) {
            return Optional.empty();
        }

        Comment comment = commentOpt.get();
        
        // Verify the author owns this comment
        if (comment.getAuthor() == null || !comment.getAuthor().getId().equals(authorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own comments");
        }

        comment.setContent(content);
        comment.setUpdatedAt(java.time.LocalDateTime.now());
        commentDao.updateComment(comment);

        // Load relationships for DTO
        loadCommentRelationships(List.of(comment));
        return Optional.of(convertToDTO(comment, false));
    }

    @Transactional
    public boolean deleteComment(Long commentId, Long authorId) {
        Optional<Comment> commentOpt = commentDao.selectCommentById(commentId);
        if (commentOpt.isEmpty()) {
            return false;
        }

        Comment comment = commentOpt.get();
        
        // Verify the author owns this comment
        if (comment.getAuthor() == null || !comment.getAuthor().getId().equals(authorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own comments");
        }

        Long postId = comment.getPost() != null ? comment.getPost().getId() : null;
        
        // Get post_id from database if not loaded
        if (postId == null && comment.getId() != null) {
            var postSql = "SELECT post_id FROM comments WHERE id = ?";
            postId = jdbcTemplate.queryForObject(postSql, Long.class, comment.getId());
        }

        // Delete the comment
        commentDao.deleteCommentById(commentId);

        // Decrement comments count on post (ensure it doesn't go below 0)
        if (postId != null) {
            jdbcTemplate.update(
                "UPDATE posts SET comments_count = GREATEST(comments_count - 1, 0) WHERE id = ?",
                postId);
        }
        
        return true;
    }

    private void loadCommentRelationships(List<Comment> comments) {
        for (Comment comment : comments) {
            // Load post
            if (comment.getId() != null) {
                try {
                    var postSql = "SELECT post_id FROM comments WHERE id = ?";
                    Long postId = jdbcTemplate.queryForObject(postSql, Long.class, comment.getId());
                    if (postId != null) {
                        postDao.selectPostById(postId).ifPresent(comment::setPost);
                    }
                } catch (org.springframework.dao.EmptyResultDataAccessException e) {
                    // Post ID not found or null - skip
                }
                
                // Load author
                try {
                    var authorSql = "SELECT author_id FROM comments WHERE id = ?";
                    Long authorId = jdbcTemplate.queryForObject(authorSql, Long.class, comment.getId());
                    if (authorId != null) {
                        authorDao.selectAuthorById(authorId).ifPresent(comment::setAuthor);
                    }
                } catch (org.springframework.dao.EmptyResultDataAccessException e) {
                    // Author ID not found or null - skip
                }
                
                // Load parent if exists
                try {
                    var parentSql = "SELECT parent_id FROM comments WHERE id = ?";
                    Long parentId = jdbcTemplate.queryForObject(parentSql, Long.class, comment.getId());
                    if (parentId != null) {
                        commentDao.selectCommentById(parentId).ifPresent(comment::setParent);
                    }
                } catch (org.springframework.dao.EmptyResultDataAccessException e) {
                    // Parent ID not found or null - skip
                }
            }
        }
    }

    private CommentDTO convertToDTO(Comment comment, boolean includeReplies) {
        AuthorDTO authorDTO = comment.getAuthor() != null
            ? AuthorDTO.fromEntity(comment.getAuthor())
            : null;

        List<CommentDTO> replies = new ArrayList<>();
        if (includeReplies) {
            replies = comment.getReplies().stream()
                .map(child -> convertToDTO(child, false))
                .collect(Collectors.toList());
        }

        return new CommentDTO(
            comment.getId().toString(),
            comment.getContent(),
            authorDTO,
            comment.getPost() != null ? comment.getPost().getId().toString() : null,
            comment.getParent() != null ? comment.getParent().getId().toString() : null,
            replies,
            comment.getLikes(),
            comment.getCreatedAt().toString()
        );
    }
    
    private Optional<Post> findPostBySlug(String slug) {
        return postDao.selectPublishedPostBySlug(slug);
    }
}
