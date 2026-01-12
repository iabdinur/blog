package com.iabdinur.rowmapper;

import com.github.javafaker.Faker;
import com.iabdinur.model.Tag;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TagRowMapperTest {

    private Faker FAKER = new Faker();

    private Tag createTestTag() {
        Long tagId = FAKER.random().nextLong();
        String name = FAKER.lorem().word();
        String slug = String.join("-", FAKER.lorem().words(2)).toLowerCase();
        String description = FAKER.lorem().sentence();
        Integer postsCount = FAKER.random().nextInt(100);
        LocalDateTime createdAt = FAKER.date().past(365, TimeUnit.DAYS).toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime updatedAt = FAKER.date().past(30, TimeUnit.DAYS).toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

        Tag tag = new Tag(tagId, name, slug, createdAt, updatedAt);
        tag.setDescription(description);
        tag.setPostsCount(postsCount);
        return tag;
    }

    @Test
    void itShouldMapRow() throws SQLException {
        // Given
        Tag tag = createTestTag();
        Long expectedId = tag.getId();
        String expectedName = tag.getName();
        String expectedSlug = tag.getSlug();
        String expectedDescription = tag.getDescription();
        Integer expectedPostsCount = tag.getPostsCount();
        LocalDateTime expectedCreatedAt = tag.getCreatedAt();
        LocalDateTime expectedUpdatedAt = tag.getUpdatedAt();

        TagRowMapper tagRowMapper = new TagRowMapper();
        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.getLong("id")).thenReturn(expectedId);
        when(resultSet.getString("name")).thenReturn(expectedName);
        when(resultSet.getString("slug")).thenReturn(expectedSlug);
        when(resultSet.getString("description")).thenReturn(expectedDescription);
        when(resultSet.getInt("posts_count")).thenReturn(expectedPostsCount);
        when(resultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(expectedCreatedAt));
        when(resultSet.getTimestamp("updated_at")).thenReturn(Timestamp.valueOf(expectedUpdatedAt));

        // When
        Tag actual = tagRowMapper.mapRow(resultSet, 1);

        // Then
        assertThat(actual.getId()).isEqualTo(expectedId);
        assertThat(actual.getName()).isEqualTo(expectedName);
        assertThat(actual.getSlug()).isEqualTo(expectedSlug);
        assertThat(actual.getDescription()).isEqualTo(expectedDescription);
        assertThat(actual.getPostsCount()).isEqualTo(expectedPostsCount);
        assertThat(actual.getCreatedAt()).isEqualTo(expectedCreatedAt);
        assertThat(actual.getUpdatedAt()).isEqualTo(expectedUpdatedAt);
    }
}
