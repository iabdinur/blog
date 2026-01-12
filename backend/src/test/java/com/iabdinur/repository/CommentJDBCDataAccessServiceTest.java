package com.iabdinur.repository;

import com.iabdinur.AbstractTestcontainers;
import com.iabdinur.model.Author;
import com.iabdinur.model.Comment;
import com.iabdinur.model.Post;
import com.iabdinur.rowmapper.AuthorRowMapper;
import com.iabdinur.rowmapper.CommentRowMapper;
import com.iabdinur.rowmapper.PostRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class CommentJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private CommentJDBCDataAccessService underTest;
    private AuthorJDBCDataAccessService authorService;
    private PostJDBCDataAccessService postService;
    private final CommentRowMapper commentRowMapper = new CommentRowMapper();
    private final AuthorRowMapper authorRowMapper = new AuthorRowMapper();
    private final PostRowMapper postRowMapper = new PostRowMapper();

    @BeforeEach
    void setUp() {
        // Clean up after each test (order matters due to foreign keys)
        getJdbcTemplate().execute("DELETE FROM post_tags");
        getJdbcTemplate().execute("DELETE FROM comments");
        getJdbcTemplate().execute("DELETE FROM posts");
        getJdbcTemplate().execute("DELETE FROM authors");
        
        authorService = new AuthorJDBCDataAccessService(getJdbcTemplate(), authorRowMapper);
        postService = new PostJDBCDataAccessService(getJdbcTemplate(), postRowMapper);
        underTest = new CommentJDBCDataAccessService(
                getJdbcTemplate(),
                commentRowMapper
        );
    }

    private Author createTestAuthor() {
        String name = FAKER.name().fullName();
        String username = FAKER.name().username();
        String email = FAKER.internet().emailAddress();
        Author author = new Author(name, username, email);
        authorService.insertAuthor(author);
        return authorService.selectAllAuthors().stream()
                .filter(a -> a.getUsername().equals(username))
                .findFirst()
                .orElseThrow();
    }

    private Post createTestPost() {
        Author author = createTestAuthor();
        String title = FAKER.lorem().sentence();
        String slug = String.join("-", FAKER.lorem().words(2)).toLowerCase();
        String content = FAKER.lorem().paragraph();
        Post post = new Post(title, slug, content, author);
        post.setIsPublished(true);
        post.setViews(0L);
        post.setLikes(0L);
        post.setCommentsCount(0);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        postService.insertPost(post);
        return postService.selectAllPosts().stream()
                .filter(p -> p.getSlug().equals(slug))
                .findFirst()
                .orElseThrow();
    }

    private Comment createTestComment() {
        Post post = createTestPost();
        Author author = createTestAuthor();
        String content = FAKER.lorem().paragraph();
        Integer likes = 0;
        LocalDateTime createdAt = FAKER.date().past(30, TimeUnit.DAYS)
                .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime updatedAt = FAKER.date().past(7, TimeUnit.DAYS)
                .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

        Comment comment = new Comment(post, author, content);
        comment.setLikes(likes);
        comment.setCreatedAt(createdAt);
        comment.setUpdatedAt(updatedAt);
        underTest.insertComment(comment);

        return underTest.selectAllComments().stream()
                .filter(c -> c.getContent().equals(content))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void itShouldSelectAllComments() {
        // Given
        Comment comment = createTestComment();

        // When
        List<Comment> actual = underTest.selectAllComments();

        // Then
        assertThat(actual).isNotEmpty();
        assertThat(actual).anyMatch(c -> c.getContent().equals(comment.getContent()));
    }

    @Test
    void itShouldSelectCommentsByPostId() {
        // Given
        Post post = createTestPost();
        Author author = createTestAuthor();
        String content = FAKER.lorem().paragraph();
        Comment comment = new Comment(post, author, content);
        comment.setLikes(0);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        underTest.insertComment(comment);

        // When
        List<Comment> actual = underTest.selectCommentsByPostId(post.getId());

        // Then
        assertThat(actual).isNotEmpty();
        // Note: post relationship is not loaded by CommentRowMapper, so we verify by content
        assertThat(actual).anyMatch(c -> c.getContent().equals(content));
    }

    @Test
    void itShouldSelectCommentById() {
        // Given
        Comment comment = createTestComment();

        // When
        Optional<Comment> actual = underTest.selectCommentById(comment.getId());

        // Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(comment.getId());
            assertThat(c.getContent()).isEqualTo(comment.getContent());
        });
    }

    @Test
    void itShouldReturnEmptyWhenSelectCommentById() {
        // Given
        Long invalidCommentId = -1L;

        // When
        Optional<Comment> actual = underTest.selectCommentById(invalidCommentId);

        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void itShouldInsertComment() {
        // Given
        Post post = createTestPost();
        Author author = createTestAuthor();
        String content = FAKER.lorem().paragraph();
        Comment comment = new Comment(post, author, content);

        // When
        underTest.insertComment(comment);

        // Then
        List<Comment> comments = underTest.selectCommentsByPostId(post.getId());
        assertThat(comments).isNotEmpty();
        assertThat(comments).anyMatch(c -> c.getContent().equals(content));
    }

    @Test
    void itShouldExistsCommentById() {
        // Given
        Comment comment = createTestComment();

        // When
        boolean exists = underTest.existsCommentById(comment.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void itShouldExistsCommentByIdWillReturnFalseWhenCommentIdNotPresent() {
        // Given
        Long commentId = -1L;

        // When
        boolean exists = underTest.existsCommentById(commentId);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void itShouldDeleteCommentById() {
        // Given
        Comment comment = createTestComment();

        // When
        underTest.deleteCommentById(comment.getId());

        // Then
        Optional<Comment> deletedComment = underTest.selectCommentById(comment.getId());
        assertThat(deletedComment).isEmpty();
    }

    @Test
    void itShouldUpdateCommentContent() {
        // Given
        Comment comment = createTestComment();
        String newContent = FAKER.lorem().paragraph();

        Comment update = new Comment();
        update.setId(comment.getId());
        update.setContent(newContent);

        // When
        underTest.updateComment(update);

        // Then
        Optional<Comment> actual = underTest.selectCommentById(comment.getId());
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(comment.getId());
            assertThat(c.getContent()).isEqualTo(newContent);
        });
    }

    @Test
    void itShouldIncrementLikes() {
        // Given
        Comment comment = createTestComment();
        Integer initialLikes = comment.getLikes();

        // When
        underTest.incrementLikes(comment.getId());

        // Then
        Optional<Comment> actual = underTest.selectCommentById(comment.getId());
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getLikes()).isEqualTo(initialLikes + 1);
        });
    }
}
