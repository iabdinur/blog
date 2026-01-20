package com.iabdinur.rowmapper;

import com.github.javafaker.Faker;
import com.iabdinur.model.Post;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PostRowMapperTest {

    private Faker FAKER = new Faker();

    private Post createTestPost() {
        Long postId = FAKER.random().nextLong();
        String title = FAKER.lorem().sentence();
        String slug = String.join("-", FAKER.lorem().words(2)).toLowerCase();
        String content = FAKER.lorem().paragraph();
        String excerpt = FAKER.lorem().sentence();
        String coverImage = FAKER.internet().image();
        String contentImage = FAKER.internet().image();
        LocalDateTime publishedAt = FAKER.date().past(30, TimeUnit.DAYS).toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        boolean isPublished = FAKER.bool().bool();
        Long views = FAKER.random().nextLong(10000);
        Long likes = FAKER.random().nextLong(1000);
        Integer commentsCount = FAKER.random().nextInt(100);
        Integer readingTime = FAKER.random().nextInt(1, 30);
        LocalDateTime createdAt = FAKER.date().past(365, TimeUnit.DAYS).toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime updatedAt = FAKER.date().past(30, TimeUnit.DAYS).toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

        Post post = new Post();
        post.setId(postId);
        post.setTitle(title);
        post.setSlug(slug);
        post.setContent(content);
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
        return post;
    }

    @Test
    void itShouldMapRow() throws SQLException {
        // Given
        Post post = createTestPost();
        Long expectedId = post.getId();
        String expectedTitle = post.getTitle();
        String expectedSlug = post.getSlug();
        String expectedContent = post.getContent();
        String expectedExcerpt = post.getExcerpt();
        String expectedCoverImage = post.getCoverImage();
        String expectedContentImage = post.getContentImage();
        LocalDateTime expectedPublishedAt = post.getPublishedAt();
        Boolean expectedIsPublished = post.getIsPublished();
        Long expectedViews = post.getViews();
        Long expectedLikes = post.getLikes();
        Integer expectedCommentsCount = post.getCommentsCount();
        Integer expectedReadingTime = post.getReadingTime();
        LocalDateTime expectedCreatedAt = post.getCreatedAt();
        LocalDateTime expectedUpdatedAt = post.getUpdatedAt();

        PostRowMapper postRowMapper = new PostRowMapper();
        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.getLong("id")).thenReturn(expectedId);
        when(resultSet.getString("title")).thenReturn(expectedTitle);
        when(resultSet.getString("slug")).thenReturn(expectedSlug);
        when(resultSet.getString("content")).thenReturn(expectedContent);
        when(resultSet.getString("excerpt")).thenReturn(expectedExcerpt);
        when(resultSet.getString("cover_image")).thenReturn(expectedCoverImage);
        when(resultSet.getString("content_image")).thenReturn(expectedContentImage);
        when(resultSet.getTimestamp("published_at")).thenReturn(Timestamp.valueOf(expectedPublishedAt));
        when(resultSet.getBoolean("is_published")).thenReturn(expectedIsPublished);
        when(resultSet.getLong("views")).thenReturn(expectedViews);
        when(resultSet.getLong("likes")).thenReturn(expectedLikes);
        when(resultSet.getInt("comments_count")).thenReturn(expectedCommentsCount);
        when(resultSet.getInt("reading_time")).thenReturn(expectedReadingTime);
        when(resultSet.wasNull()).thenReturn(false);
        when(resultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(expectedCreatedAt));
        when(resultSet.getTimestamp("updated_at")).thenReturn(Timestamp.valueOf(expectedUpdatedAt));

        // When
        Post actual = postRowMapper.mapRow(resultSet, 1);

        // Then
        assertThat(actual.getId()).isEqualTo(expectedId);
        assertThat(actual.getTitle()).isEqualTo(expectedTitle);
        assertThat(actual.getSlug()).isEqualTo(expectedSlug);
        assertThat(actual.getContent()).isEqualTo(expectedContent);
        assertThat(actual.getExcerpt()).isEqualTo(expectedExcerpt);
        assertThat(actual.getCoverImage()).isEqualTo(expectedCoverImage);
        assertThat(actual.getContentImage()).isEqualTo(expectedContentImage);
        assertThat(actual.getPublishedAt()).isEqualTo(expectedPublishedAt);
        assertThat(actual.getIsPublished()).isEqualTo(expectedIsPublished);
        assertThat(actual.getViews()).isEqualTo(expectedViews);
        assertThat(actual.getLikes()).isEqualTo(expectedLikes);
        assertThat(actual.getCommentsCount()).isEqualTo(expectedCommentsCount);
        assertThat(actual.getReadingTime()).isEqualTo(expectedReadingTime);
        assertThat(actual.getCreatedAt()).isEqualTo(expectedCreatedAt);
        assertThat(actual.getUpdatedAt()).isEqualTo(expectedUpdatedAt);
    }

    @Test
    void itShouldMapRowWithNullPublishedAt() throws SQLException {
        // Given
        Post post = createTestPost();
        PostRowMapper postRowMapper = new PostRowMapper();
        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.getLong("id")).thenReturn(post.getId());
        when(resultSet.getString("title")).thenReturn(post.getTitle());
        when(resultSet.getString("slug")).thenReturn(post.getSlug());
        when(resultSet.getString("content")).thenReturn(post.getContent());
        when(resultSet.getString("excerpt")).thenReturn(post.getExcerpt());
        when(resultSet.getString("cover_image")).thenReturn(post.getCoverImage());
        when(resultSet.getString("content_image")).thenReturn(post.getContentImage());
        when(resultSet.getTimestamp("published_at")).thenReturn(null);
        when(resultSet.getBoolean("is_published")).thenReturn(post.getIsPublished());
        when(resultSet.getLong("views")).thenReturn(post.getViews());
        when(resultSet.getLong("likes")).thenReturn(post.getLikes());
        when(resultSet.getInt("comments_count")).thenReturn(post.getCommentsCount());
        when(resultSet.getInt("reading_time")).thenReturn(post.getReadingTime());
        when(resultSet.wasNull()).thenReturn(false);
        when(resultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(post.getCreatedAt()));
        when(resultSet.getTimestamp("updated_at")).thenReturn(Timestamp.valueOf(post.getUpdatedAt()));

        // When
        Post actual = postRowMapper.mapRow(resultSet, 1);

        // Then
        assertThat(actual.getPublishedAt()).isNull();
    }

    @Test
    void itShouldMapRowWithNullReadingTime() throws SQLException {
        // Given
        Post post = createTestPost();
        PostRowMapper postRowMapper = new PostRowMapper();
        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.getLong("id")).thenReturn(post.getId());
        when(resultSet.getString("title")).thenReturn(post.getTitle());
        when(resultSet.getString("slug")).thenReturn(post.getSlug());
        when(resultSet.getString("content")).thenReturn(post.getContent());
        when(resultSet.getString("excerpt")).thenReturn(post.getExcerpt());
        when(resultSet.getString("cover_image")).thenReturn(post.getCoverImage());
        when(resultSet.getString("content_image")).thenReturn(post.getContentImage());
        when(resultSet.getTimestamp("published_at")).thenReturn(Timestamp.valueOf(post.getPublishedAt()));
        when(resultSet.getBoolean("is_published")).thenReturn(post.getIsPublished());
        when(resultSet.getLong("views")).thenReturn(post.getViews());
        when(resultSet.getLong("likes")).thenReturn(post.getLikes());
        when(resultSet.getInt("comments_count")).thenReturn(post.getCommentsCount());
        when(resultSet.getInt("reading_time")).thenReturn(0);
        when(resultSet.wasNull()).thenReturn(true);
        when(resultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(post.getCreatedAt()));
        when(resultSet.getTimestamp("updated_at")).thenReturn(Timestamp.valueOf(post.getUpdatedAt()));

        // When
        Post actual = postRowMapper.mapRow(resultSet, 1);

        // Then
        assertThat(actual.getReadingTime()).isNull();
    }
}
