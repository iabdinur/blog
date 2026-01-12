package com.iabdinur.rowmapper;

import com.github.javafaker.Faker;
import com.iabdinur.model.Comment;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommentRowMapperTest {

    private Faker FAKER = new Faker();

    private Comment createTestComment() {
        Long commentId = FAKER.random().nextLong();
        String content = FAKER.lorem().paragraph();
        Integer likes = FAKER.random().nextInt(100);
        LocalDateTime createdAt = FAKER.date().past(30, TimeUnit.DAYS).toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime updatedAt = FAKER.date().past(7, TimeUnit.DAYS).toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setContent(content);
        comment.setLikes(likes);
        comment.setCreatedAt(createdAt);
        comment.setUpdatedAt(updatedAt);
        return comment;
    }

    @Test
    void itShouldMapRow() throws SQLException {
        // Given
        Comment comment = createTestComment();
        Long expectedId = comment.getId();
        String expectedContent = comment.getContent();
        Integer expectedLikes = comment.getLikes();
        LocalDateTime expectedCreatedAt = comment.getCreatedAt();
        LocalDateTime expectedUpdatedAt = comment.getUpdatedAt();

        CommentRowMapper commentRowMapper = new CommentRowMapper();
        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.getLong("id")).thenReturn(expectedId);
        when(resultSet.getString("content")).thenReturn(expectedContent);
        when(resultSet.getInt("likes")).thenReturn(expectedLikes);
        when(resultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(expectedCreatedAt));
        when(resultSet.getTimestamp("updated_at")).thenReturn(Timestamp.valueOf(expectedUpdatedAt));

        // When
        Comment actual = commentRowMapper.mapRow(resultSet, 1);

        // Then
        assertThat(actual.getId()).isEqualTo(expectedId);
        assertThat(actual.getContent()).isEqualTo(expectedContent);
        assertThat(actual.getLikes()).isEqualTo(expectedLikes);
        assertThat(actual.getCreatedAt()).isEqualTo(expectedCreatedAt);
        assertThat(actual.getUpdatedAt()).isEqualTo(expectedUpdatedAt);
    }
}
