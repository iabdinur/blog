package com.iabdinur.service;

import com.github.javafaker.Faker;
import com.iabdinur.dao.TagDao;
import com.iabdinur.dto.CreateTagRequest;
import com.iabdinur.dto.TagDTO;
import com.iabdinur.model.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TagServiceTest {

    private TagService underTest;
    private AutoCloseable autoCloseable;

    @Mock
    private TagDao tagDao;
    @Mock
    private JdbcTemplate jdbcTemplate;

    private final Faker FAKER = new Faker();

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new TagService(tagDao, jdbcTemplate);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    private Tag createTestTag() {
        Tag tag = new Tag();
        tag.setId(FAKER.random().nextLong());
        tag.setName(FAKER.lorem().word());
        tag.setSlug(String.join("-", FAKER.lorem().words(2)).toLowerCase());
        tag.setDescription(FAKER.lorem().sentence());
        tag.setPostsCount(0);
        tag.setCreatedAt(LocalDateTime.now());
        tag.setUpdatedAt(LocalDateTime.now());
        return tag;
    }

    @Test
    void itShouldGetAllTags() {
        // Given
        List<Tag> tags = new ArrayList<>();
        Tag tag = createTestTag();
        tags.add(tag);
        
        when(tagDao.selectAllTags()).thenReturn(tags);

        // When
        List<TagDTO> result = underTest.getAllTags();

        // Then
        verify(tagDao).selectAllTags();
        assertThat(result).hasSize(1);
    }

    @Test
    void itShouldGetTagBySlug() {
        // Given
        Tag tag = createTestTag();
        when(tagDao.selectTagBySlug(tag.getSlug())).thenReturn(Optional.of(tag));

        // When
        Optional<TagDTO> result = underTest.getTagBySlug(tag.getSlug());

        // Then
        verify(tagDao).selectTagBySlug(tag.getSlug());
        assertThat(result).isPresent();
        assertEquals(tag.getSlug(), result.get().slug());
    }

    @Test
    void itShouldReturnEmptyWhenTagNotFound() {
        // Given
        String slug = String.join("-", FAKER.lorem().words(2)).toLowerCase();
        when(tagDao.selectTagBySlug(slug)).thenReturn(Optional.empty());

        // When
        Optional<TagDTO> result = underTest.getTagBySlug(slug);

        // Then
        verify(tagDao).selectTagBySlug(slug);
        assertThat(result).isEmpty();
    }

    @Test
    void itShouldSearchTags() {
        // Given
        String query = FAKER.lorem().word();
        List<Tag> tags = new ArrayList<>();
        when(jdbcTemplate.query(anyString(), isA(org.springframework.jdbc.core.RowMapper.class), eq(query), eq(query), eq(query))).thenReturn(tags);

        // When
        List<TagDTO> result = underTest.searchTags(query);

        // Then
        verify(jdbcTemplate).query(anyString(), isA(org.springframework.jdbc.core.RowMapper.class), eq(query), eq(query), eq(query));
        assertThat(result).isNotNull();
    }

    @Test
    void itShouldCreateTag() {
        // Given
        CreateTagRequest request = new CreateTagRequest(
                FAKER.lorem().word(),
                String.join("-", FAKER.lorem().words(2)).toLowerCase(),
                FAKER.lorem().sentence()
        );

        // Mock insertTag to set an ID on the tag
        doAnswer(invocation -> {
            Tag tag = invocation.getArgument(0);
            tag.setId(FAKER.random().nextLong());
            return null;
        }).when(tagDao).insertTag(any(Tag.class));

        // When
        TagDTO result = underTest.createTag(request);

        // Then
        ArgumentCaptor<Tag> tagArgumentCaptor = ArgumentCaptor.forClass(Tag.class);
        verify(tagDao).insertTag(tagArgumentCaptor.capture());
        Tag capturedTag = tagArgumentCaptor.getValue();
        
        assertEquals(request.name(), capturedTag.getName());
        assertEquals(request.slug(), capturedTag.getSlug());
        assertEquals(request.description(), capturedTag.getDescription());
        assertEquals(0, capturedTag.getPostsCount());
        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
    }

    @Test
    void itShouldUpdateTag() {
        // Given
        Tag tag = createTestTag();
        String newName = "Updated " + FAKER.lorem().word();
        
        CreateTagRequest request = new CreateTagRequest(
                newName,
                tag.getSlug(),
                "Updated " + FAKER.lorem().sentence()
        );

        when(tagDao.selectTagBySlug(tag.getSlug())).thenReturn(Optional.of(tag));

        // When
        Optional<TagDTO> result = underTest.updateTag(tag.getSlug(), request);

        // Then
        ArgumentCaptor<Tag> tagArgumentCaptor = ArgumentCaptor.forClass(Tag.class);
        verify(tagDao).updateTag(tagArgumentCaptor.capture());
        Tag capturedTag = tagArgumentCaptor.getValue();
        
        assertEquals(newName, capturedTag.getName());
        assertThat(result).isPresent();
    }

    @Test
    void itShouldReturnEmptyWhenUpdatingNonExistentTag() {
        // Given
        String slug = String.join("-", FAKER.lorem().words(2)).toLowerCase();
        CreateTagRequest request = new CreateTagRequest(
                FAKER.lorem().word(),
                slug,
                FAKER.lorem().sentence()
        );

        when(tagDao.selectTagBySlug(slug)).thenReturn(Optional.empty());

        // When
        Optional<TagDTO> result = underTest.updateTag(slug, request);

        // Then
        verify(tagDao).selectTagBySlug(slug);
        verify(tagDao, never()).updateTag(any());
        assertThat(result).isEmpty();
    }

    @Test
    void itShouldDeleteTag() {
        // Given
        Tag tag = createTestTag();
        when(tagDao.selectTagBySlug(tag.getSlug())).thenReturn(Optional.of(tag));

        // When
        boolean result = underTest.deleteTag(tag.getSlug());

        // Then
        verify(tagDao).selectTagBySlug(tag.getSlug());
        verify(tagDao).deleteTagById(tag.getId());
        assertThat(result).isTrue();
    }

    @Test
    void itShouldReturnFalseWhenDeletingNonExistentTag() {
        // Given
        String slug = String.join("-", FAKER.lorem().words(2)).toLowerCase();
        when(tagDao.selectTagBySlug(slug)).thenReturn(Optional.empty());

        // When
        boolean result = underTest.deleteTag(slug);

        // Then
        verify(tagDao).selectTagBySlug(slug);
        verify(tagDao, never()).deleteTagById(anyLong());
        assertThat(result).isFalse();
    }
}
