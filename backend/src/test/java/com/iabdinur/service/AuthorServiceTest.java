package com.iabdinur.service;

import com.github.javafaker.Faker;
import com.iabdinur.dao.AuthorDao;
import com.iabdinur.dto.AuthorDTO;
import com.iabdinur.dto.CreateAuthorRequest;
import com.iabdinur.model.Author;
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

class AuthorServiceTest {

    private AuthorService underTest;
    private AutoCloseable autoCloseable;

    @Mock
    private AuthorDao authorDao;
    @Mock
    private JdbcTemplate jdbcTemplate;

    private final Faker FAKER = new Faker();

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new AuthorService(authorDao, jdbcTemplate);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    private Author createTestAuthor() {
        Author author = new Author();
        author.setId(FAKER.random().nextLong());
        author.setName(FAKER.name().fullName());
        author.setUsername(FAKER.name().username());
        author.setEmail(FAKER.internet().emailAddress());
        author.setBio(FAKER.lorem().sentence());
        author.setCreatedAt(LocalDateTime.now());
        author.setUpdatedAt(LocalDateTime.now());
        return author;
    }

    @Test
    void itShouldGetAllAuthors() {
        // Given
        List<Author> authors = new ArrayList<>();
        Author author = createTestAuthor();
        authors.add(author);
        
        when(authorDao.selectAllAuthors()).thenReturn(authors);

        // When
        List<AuthorDTO> result = underTest.getAllAuthors();

        // Then
        verify(authorDao).selectAllAuthors();
        assertThat(result).hasSize(1);
    }

    @Test
    void itShouldGetAuthorByUsername() {
        // Given
        Author author = createTestAuthor();
        when(authorDao.selectAuthorByUsername(author.getUsername())).thenReturn(Optional.of(author));

        // When
        Optional<AuthorDTO> result = underTest.getAuthorByUsername(author.getUsername());

        // Then
        verify(authorDao).selectAuthorByUsername(author.getUsername());
        assertThat(result).isPresent();
        assertEquals(author.getUsername(), result.get().username());
    }

    @Test
    void itShouldReturnEmptyWhenAuthorNotFound() {
        // Given
        String username = FAKER.name().username();
        when(authorDao.selectAuthorByUsername(username)).thenReturn(Optional.empty());

        // When
        Optional<AuthorDTO> result = underTest.getAuthorByUsername(username);

        // Then
        verify(authorDao).selectAuthorByUsername(username);
        assertThat(result).isEmpty();
    }

    @Test
    void itShouldGetAuthorById() {
        // Given
        Author author = createTestAuthor();
        when(authorDao.selectAuthorById(author.getId())).thenReturn(Optional.of(author));

        // When
        Optional<AuthorDTO> result = underTest.getAuthorById(author.getId().toString());

        // Then
        verify(authorDao).selectAuthorById(author.getId());
        assertThat(result).isPresent();
        assertEquals(author.getId().toString(), result.get().id());
    }

    @Test
    void itShouldReturnEmptyWhenAuthorIdNotFound() {
        // Given
        Long authorId = FAKER.random().nextLong();
        when(authorDao.selectAuthorById(authorId)).thenReturn(Optional.empty());

        // When
        Optional<AuthorDTO> result = underTest.getAuthorById(authorId.toString());

        // Then
        verify(authorDao).selectAuthorById(authorId);
        assertThat(result).isEmpty();
    }

    @Test
    void itShouldReturnEmptyWhenAuthorIdIsInvalid() {
        // When
        Optional<AuthorDTO> result = underTest.getAuthorById("invalid-id");

        // Then
        verify(authorDao, never()).selectAuthorById(anyLong());
        assertThat(result).isEmpty();
    }

    @Test
    void itShouldSearchAuthors() {
        // Given
        String query = FAKER.lorem().word();
        List<Author> authors = new ArrayList<>();
        when(jdbcTemplate.query(anyString(), isA(org.springframework.jdbc.core.RowMapper.class), eq(query), eq(query), eq(query))).thenReturn(authors);

        // When
        List<AuthorDTO> result = underTest.searchAuthors(query);

        // Then
        verify(jdbcTemplate).query(anyString(), isA(org.springframework.jdbc.core.RowMapper.class), eq(query), eq(query), eq(query));
        assertThat(result).isNotNull();
    }

    @Test
    void itShouldCreateAuthor() {
        // Given
        CreateAuthorRequest request = new CreateAuthorRequest(
                FAKER.name().fullName(),
                FAKER.name().username(),
                FAKER.internet().emailAddress(),
                FAKER.lorem().sentence(),
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // Mock insertAuthor to set an ID on the author
        doAnswer(invocation -> {
            Author author = invocation.getArgument(0);
            author.setId(FAKER.random().nextLong());
            return null;
        }).when(authorDao).insertAuthor(any(Author.class));

        // When
        AuthorDTO result = underTest.createAuthor(request);

        // Then
        ArgumentCaptor<Author> authorArgumentCaptor = ArgumentCaptor.forClass(Author.class);
        verify(authorDao).insertAuthor(authorArgumentCaptor.capture());
        Author capturedAuthor = authorArgumentCaptor.getValue();
        
        assertEquals(request.name(), capturedAuthor.getName());
        assertEquals(request.username(), capturedAuthor.getUsername());
        assertEquals(request.email(), capturedAuthor.getEmail());
        assertEquals(request.bio(), capturedAuthor.getBio());
        assertEquals(0, capturedAuthor.getFollowersCount());
        assertEquals(0, capturedAuthor.getPostsCount());
        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
    }

    @Test
    void itShouldUpdateAuthor() {
        // Given
        Author author = createTestAuthor();
        String newName = "Updated " + FAKER.name().fullName();
        String newBio = "Updated " + FAKER.lorem().sentence();
        
        CreateAuthorRequest request = new CreateAuthorRequest(
                newName,
                author.getUsername(),
                author.getEmail(),
                newBio,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(authorDao.selectAuthorByUsername(author.getUsername())).thenReturn(Optional.of(author));

        // When
        Optional<AuthorDTO> result = underTest.updateAuthor(author.getUsername(), request);

        // Then
        ArgumentCaptor<Author> authorArgumentCaptor = ArgumentCaptor.forClass(Author.class);
        verify(authorDao).updateAuthor(authorArgumentCaptor.capture());
        Author capturedAuthor = authorArgumentCaptor.getValue();
        
        assertEquals(newName, capturedAuthor.getName());
        assertEquals(newBio, capturedAuthor.getBio());
        assertThat(result).isPresent();
    }

    @Test
    void itShouldReturnEmptyWhenUpdatingNonExistentAuthor() {
        // Given
        String username = FAKER.name().username();
        CreateAuthorRequest request = new CreateAuthorRequest(
                FAKER.name().fullName(),
                username,
                FAKER.internet().emailAddress(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(authorDao.selectAuthorByUsername(username)).thenReturn(Optional.empty());

        // When
        Optional<AuthorDTO> result = underTest.updateAuthor(username, request);

        // Then
        verify(authorDao).selectAuthorByUsername(username);
        verify(authorDao, never()).updateAuthor(any());
        assertThat(result).isEmpty();
    }

    @Test
    void itShouldDeleteAuthor() {
        // Given
        Author author = createTestAuthor();
        when(authorDao.selectAuthorByUsername(author.getUsername())).thenReturn(Optional.of(author));

        // When
        boolean result = underTest.deleteAuthor(author.getUsername());

        // Then
        verify(authorDao).selectAuthorByUsername(author.getUsername());
        verify(authorDao).deleteAuthorById(author.getId());
        assertThat(result).isTrue();
    }

    @Test
    void itShouldReturnFalseWhenDeletingNonExistentAuthor() {
        // Given
        String username = FAKER.name().username();
        when(authorDao.selectAuthorByUsername(username)).thenReturn(Optional.empty());

        // When
        boolean result = underTest.deleteAuthor(username);

        // Then
        verify(authorDao).selectAuthorByUsername(username);
        verify(authorDao, never()).deleteAuthorById(anyLong());
        assertThat(result).isFalse();
    }
}
