package com.iabdinur.repository;

import com.iabdinur.AbstractTestcontainers;
import com.iabdinur.model.Author;
import com.iabdinur.model.Post;
import com.iabdinur.repository.AuthorJDBCDataAccessService;
import com.iabdinur.rowmapper.AuthorRowMapper;
import com.iabdinur.rowmapper.PostRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class PostJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private PostJDBCDataAccessService underTest;
    private AuthorJDBCDataAccessService authorService;
    private final PostRowMapper postRowMapper = new PostRowMapper();
    private final AuthorRowMapper authorRowMapper = new AuthorRowMapper();

    @BeforeEach
    void setUp() {
        // Clean up after each test (order matters due to foreign keys)
        getJdbcTemplate().execute("DELETE FROM post_tags");
        getJdbcTemplate().execute("DELETE FROM comments");
        getJdbcTemplate().execute("DELETE FROM posts");
        getJdbcTemplate().execute("DELETE FROM authors");
        
        authorService = new AuthorJDBCDataAccessService(getJdbcTemplate(), authorRowMapper);
        underTest = new PostJDBCDataAccessService(
                getJdbcTemplate(),
                postRowMapper
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
        String excerpt = FAKER.lorem().sentence();
        String coverImage = FAKER.internet().image();
        String contentImage = FAKER.internet().image();
        LocalDateTime publishedAt = FAKER.date().past(30, TimeUnit.DAYS)
                .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        boolean isPublished = FAKER.bool().bool();
        Long views = 0L;
        Long likes = 0L;
        Integer commentsCount = 0;
        Integer readingTime = FAKER.random().nextInt(1, 30);
        LocalDateTime createdAt = FAKER.date().past(365, TimeUnit.DAYS)
                .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime updatedAt = FAKER.date().past(30, TimeUnit.DAYS)
                .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

        Post post = new Post(title, slug, content, author);
        post.setExcerpt(excerpt);
        post.setCoverImage(coverImage);
        post.setContentImage(contentImage);
        post.setPublishedAt(publishedAt);
        post.setIsPublished(isPublished);
        post.setViews(views);
        post.setLikes(likes);
        post.setCommentsCount(commentsCount);
        post.setReadingTime(readingTime);
        post.setCreatedAt(createdAt);
        post.setUpdatedAt(updatedAt);
        underTest.insertPost(post);

        return underTest.selectAllPosts().stream()
                .filter(p -> p.getSlug().equals(slug))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void itShouldSelectAllPosts() {
        // Given
        Post post = createTestPost();

        // When
        List<Post> actual = underTest.selectAllPosts();

        // Then
        assertThat(actual).isNotEmpty();
        assertThat(actual).anyMatch(p -> p.getSlug().equals(post.getSlug()));
    }

    @Test
    void itShouldSelectPostById() {
        // Given
        Post post = createTestPost();

        // When
        Optional<Post> actual = underTest.selectPostById(post.getId());

        // Then
        assertThat(actual).isPresent().hasValueSatisfying(p -> {
            assertThat(p.getId()).isEqualTo(post.getId());
            assertThat(p.getSlug()).isEqualTo(post.getSlug());
            assertThat(p.getTitle()).isEqualTo(post.getTitle());
        });
    }

    @Test
    void itShouldSelectPostBySlug() {
        // Given
        Post post = createTestPost();

        // When
        Optional<Post> actual = underTest.selectPostBySlug(post.getSlug());

        // Then
        assertThat(actual).isPresent().hasValueSatisfying(p -> {
            assertThat(p.getSlug()).isEqualTo(post.getSlug());
        });
    }

    @Test
    void itShouldSelectPublishedPostBySlug() {
        // Given
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
        underTest.insertPost(post);

        // When
        Optional<Post> actual = underTest.selectPublishedPostBySlug(slug);

        // Then
        assertThat(actual).isPresent().hasValueSatisfying(p -> {
            assertThat(p.getSlug()).isEqualTo(slug);
            assertThat(p.getIsPublished()).isTrue();
        });
    }

    @Test
    void itShouldReturnEmptyWhenSelectPostById() {
        // Given
        Long invalidPostId = -1L;

        // When
        Optional<Post> actual = underTest.selectPostById(invalidPostId);

        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void itShouldInsertPost() {
        // Given
        Author author = createTestAuthor();
        String title = FAKER.lorem().sentence();
        String slug = String.join("-", FAKER.lorem().words(2)).toLowerCase();
        String content = FAKER.lorem().paragraph();
        Post post = new Post(title, slug, content, author);

        // When
        underTest.insertPost(post);

        // Then
        Optional<Post> actual = underTest.selectPostBySlug(slug);
        assertThat(actual).isPresent().hasValueSatisfying(p -> {
            assertThat(p.getTitle()).isEqualTo(title);
            assertThat(p.getSlug()).isEqualTo(slug);
            assertThat(p.getContent()).isEqualTo(content);
            assertThat(p.getViews()).isEqualTo(0L);
            assertThat(p.getLikes()).isEqualTo(0L);
            assertThat(p.getCommentsCount()).isEqualTo(0);
        });
    }

    @Test
    void itShouldExistsPostWithSlug() {
        // Given
        Post post = createTestPost();

        // When
        boolean exists = underTest.existsPostWithSlug(post.getSlug());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void itShouldExistsPostWithSlugReturnFalseWhenDoesNotExist() {
        // Given
        String slug = String.join("-", FAKER.lorem().words(2)).toLowerCase();

        // When
        boolean exists = underTest.existsPostWithSlug(slug);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void itShouldExistsPostById() {
        // Given
        Post post = createTestPost();

        // When
        boolean exists = underTest.existsPostById(post.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void itShouldExistsPostByIdWillReturnFalseWhenPostIdNotPresent() {
        // Given
        Long postId = -1L;

        // When
        boolean exists = underTest.existsPostById(postId);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void itShouldDeletePostById() {
        // Given
        Post post = createTestPost();

        // When
        underTest.deletePostById(post.getId());

        // Then
        Optional<Post> deletedPost = underTest.selectPostById(post.getId());
        assertThat(deletedPost).isEmpty();
    }

    @Test
    void itShouldUpdatePostTitle() {
        // Given
        Post post = createTestPost();
        String newTitle = FAKER.lorem().sentence();

        Post update = new Post();
        update.setId(post.getId());
        update.setTitle(newTitle);

        // When
        underTest.updatePost(update);

        // Then
        Optional<Post> actual = underTest.selectPostById(post.getId());
        assertThat(actual).isPresent().hasValueSatisfying(p -> {
            assertThat(p.getId()).isEqualTo(post.getId());
            assertThat(p.getTitle()).isEqualTo(newTitle);
        });
    }

    @Test
    void itShouldIncrementViews() {
        // Given
        Post post = createTestPost();
        Long initialViews = post.getViews();

        // When
        underTest.incrementViews(post.getId());

        // Then
        Optional<Post> actual = underTest.selectPostById(post.getId());
        assertThat(actual).isPresent().hasValueSatisfying(p -> {
            assertThat(p.getViews()).isEqualTo(initialViews + 1);
        });
    }

    @Test
    void itShouldIncrementLikes() {
        // Given
        Post post = createTestPost();
        Long initialLikes = post.getLikes();

        // When
        underTest.incrementLikes(post.getId());

        // Then
        Optional<Post> actual = underTest.selectPostById(post.getId());
        assertThat(actual).isPresent().hasValueSatisfying(p -> {
            assertThat(p.getLikes()).isEqualTo(initialLikes + 1);
        });
    }

    @Test
    void itShouldDecrementLikes() {
        // Given
        Post post = createTestPost();
        // Manually update likes in database
        getJdbcTemplate().update("UPDATE posts SET likes = ? WHERE id = ?", 5L, post.getId());

        // When
        underTest.decrementLikes(post.getId());

        // Then
        Optional<Post> actual = underTest.selectPostById(post.getId());
        assertThat(actual).isPresent().hasValueSatisfying(p -> {
            assertThat(p.getLikes()).isEqualTo(4L);
        });
    }

    @Test
    void itShouldDecrementLikesNotGoBelowZero() {
        // Given
        Post post = createTestPost();
        // Manually update likes in database
        getJdbcTemplate().update("UPDATE posts SET likes = ? WHERE id = ?", 0L, post.getId());

        // When
        underTest.decrementLikes(post.getId());

        // Then
        Optional<Post> actual = underTest.selectPostById(post.getId());
        assertThat(actual).isPresent().hasValueSatisfying(p -> {
            assertThat(p.getLikes()).isEqualTo(0L);
        });
    }

    @Test
    void itShouldCountPublishedPosts() {
        // Given
        Author author = createTestAuthor();
        
        // Create published post
        String title1 = FAKER.lorem().sentence();
        String slug1 = String.join("-", FAKER.lorem().words(2)).toLowerCase();
        Post post1 = new Post(title1, slug1, FAKER.lorem().paragraph(), author);
        post1.setIsPublished(true);
        post1.setViews(0L);
        post1.setLikes(0L);
        post1.setCommentsCount(0);
        post1.setCreatedAt(LocalDateTime.now());
        post1.setUpdatedAt(LocalDateTime.now());
        underTest.insertPost(post1);
        
        // Create unpublished post
        String title2 = FAKER.lorem().sentence();
        String slug2 = String.join("-", FAKER.lorem().words(2)).toLowerCase();
        Post post2 = new Post(title2, slug2, FAKER.lorem().paragraph(), author);
        post2.setIsPublished(false);
        post2.setViews(0L);
        post2.setLikes(0L);
        post2.setCommentsCount(0);
        post2.setCreatedAt(LocalDateTime.now());
        post2.setUpdatedAt(LocalDateTime.now());
        underTest.insertPost(post2);

        // When
        long count = underTest.countPublishedPosts();

        // Then
        assertThat(count).isGreaterThanOrEqualTo(1);
    }
}
