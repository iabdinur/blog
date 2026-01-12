package com.iabdinur.repository;

import com.iabdinur.AbstractTestcontainers;
import com.iabdinur.model.Tag;
import com.iabdinur.rowmapper.TagRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class TagJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private TagJDBCDataAccessService underTest;
    private final TagRowMapper tagRowMapper = new TagRowMapper();

    @BeforeEach
    void setUp() {
        // Clean up after each test
        getJdbcTemplate().execute("DELETE FROM post_tags");
        getJdbcTemplate().execute("DELETE FROM tags");
        underTest = new TagJDBCDataAccessService(
                getJdbcTemplate(),
                tagRowMapper
        );
    }

    private Tag createTestTag() {
        String name = FAKER.lorem().word();
        String slug = String.join("-", FAKER.lorem().words(2)).toLowerCase();
        String description = FAKER.lorem().sentence();
        Integer postsCount = FAKER.random().nextInt(100);
        LocalDateTime createdAt = FAKER.date().past(365, TimeUnit.DAYS)
                .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime updatedAt = FAKER.date().past(30, TimeUnit.DAYS)
                .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

        Tag tag = new Tag(name, slug);
        tag.setDescription(description);
        tag.setPostsCount(postsCount);
        tag.setCreatedAt(createdAt);
        tag.setUpdatedAt(updatedAt);
        underTest.insertTag(tag);

        return underTest.selectAllTags().stream()
                .filter(t -> t.getSlug().equals(slug))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void itShouldSelectAllTags() {
        // Given
        Tag tag = createTestTag();

        // When
        List<Tag> actual = underTest.selectAllTags();

        // Then
        assertThat(actual).isNotEmpty();
        assertThat(actual).anyMatch(t -> t.getSlug().equals(tag.getSlug()));
    }

    @Test
    void itShouldSelectTagById() {
        // Given
        Tag tag = createTestTag();

        // When
        Optional<Tag> actual = underTest.selectTagById(tag.getId());

        // Then
        assertThat(actual).isPresent().hasValueSatisfying(t -> {
            assertThat(t.getId()).isEqualTo(tag.getId());
            assertThat(t.getName()).isEqualTo(tag.getName());
            assertThat(t.getSlug()).isEqualTo(tag.getSlug());
        });
    }

    @Test
    void itShouldSelectTagBySlug() {
        // Given
        Tag tag = createTestTag();

        // When
        Optional<Tag> actual = underTest.selectTagBySlug(tag.getSlug());

        // Then
        assertThat(actual).isPresent().hasValueSatisfying(t -> {
            assertThat(t.getSlug()).isEqualTo(tag.getSlug());
        });
    }

    @Test
    void itShouldReturnEmptyWhenSelectTagById() {
        // Given
        Long invalidTagId = -1L;

        // When
        Optional<Tag> actual = underTest.selectTagById(invalidTagId);

        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void itShouldInsertTag() {
        // Given
        String name = FAKER.lorem().word();
        String slug = String.join("-", FAKER.lorem().words(2)).toLowerCase();
        Tag tag = new Tag(name, slug);

        // When
        underTest.insertTag(tag);

        // Then
        Optional<Tag> actual = underTest.selectTagBySlug(slug);
        assertThat(actual).isPresent().hasValueSatisfying(t -> {
            assertThat(t.getName()).isEqualTo(name);
            assertThat(t.getSlug()).isEqualTo(slug);
        });
    }

    @Test
    void itShouldExistsTagWithSlug() {
        // Given
        Tag tag = createTestTag();

        // When
        boolean exists = underTest.existsTagWithSlug(tag.getSlug());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void itShouldExistsTagWithSlugReturnFalseWhenDoesNotExist() {
        // Given
        String slug = String.join("-", FAKER.lorem().words(2)).toLowerCase();

        // When
        boolean exists = underTest.existsTagWithSlug(slug);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void itShouldExistsTagWithName() {
        // Given
        Tag tag = createTestTag();

        // When
        boolean exists = underTest.existsTagWithName(tag.getName());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void itShouldExistsTagWithNameReturnFalseWhenDoesNotExist() {
        // Given
        String name = FAKER.lorem().word();

        // When
        boolean exists = underTest.existsTagWithName(name);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void itShouldExistsTagById() {
        // Given
        Tag tag = createTestTag();

        // When
        boolean exists = underTest.existsTagById(tag.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void itShouldExistsTagByIdWillReturnFalseWhenTagIdNotPresent() {
        // Given
        Long tagId = -1L;

        // When
        boolean exists = underTest.existsTagById(tagId);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void itShouldDeleteTagById() {
        // Given
        Tag tag = createTestTag();

        // When
        underTest.deleteTagById(tag.getId());

        // Then
        Optional<Tag> deletedTag = underTest.selectTagById(tag.getId());
        assertThat(deletedTag).isEmpty();
    }

    @Test
    void itShouldUpdateTagName() {
        // Given
        Tag tag = createTestTag();
        String newName = FAKER.lorem().word();

        Tag update = new Tag();
        update.setId(tag.getId());
        update.setName(newName);

        // When
        underTest.updateTag(update);

        // Then
        Optional<Tag> actual = underTest.selectTagById(tag.getId());
        assertThat(actual).isPresent().hasValueSatisfying(t -> {
            assertThat(t.getId()).isEqualTo(tag.getId());
            assertThat(t.getName()).isEqualTo(newName);
        });
    }

    @Test
    void itShouldUpdateTagSlug() {
        // Given
        Tag tag = createTestTag();
        String newSlug = String.join("-", FAKER.lorem().words(2)).toLowerCase();

        Tag update = new Tag();
        update.setId(tag.getId());
        update.setSlug(newSlug);

        // When
        underTest.updateTag(update);

        // Then
        Optional<Tag> actual = underTest.selectTagById(tag.getId());
        assertThat(actual).isPresent().hasValueSatisfying(t -> {
            assertThat(t.getId()).isEqualTo(tag.getId());
            assertThat(t.getSlug()).isEqualTo(newSlug);
        });
    }

    @Test
    void itShouldNotUpdateWhenNothingToUpdate() {
        // Given
        Tag tag = createTestTag();

        Tag update = new Tag();
        update.setId(tag.getId());

        // When
        underTest.updateTag(update);

        // Then
        Optional<Tag> actual = underTest.selectTagById(tag.getId());
        assertThat(actual).isPresent().hasValueSatisfying(t -> {
            assertThat(t.getId()).isEqualTo(tag.getId());
            assertThat(t.getName()).isEqualTo(tag.getName());
            assertThat(t.getSlug()).isEqualTo(tag.getSlug());
        });
    }
}
