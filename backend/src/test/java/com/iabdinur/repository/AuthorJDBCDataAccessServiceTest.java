package com.iabdinur.repository;

import com.iabdinur.AbstractTestcontainers;
import com.iabdinur.model.Author;
import com.iabdinur.rowmapper.AuthorRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private AuthorJDBCDataAccessService underTest;
    private final AuthorRowMapper authorRowMapper = new AuthorRowMapper();

    @BeforeEach
    void setUp() {
        // Clean up after each test (order matters due to foreign keys)
        getJdbcTemplate().execute("DELETE FROM post_tags");
        getJdbcTemplate().execute("DELETE FROM comments");
        getJdbcTemplate().execute("DELETE FROM posts");
        getJdbcTemplate().execute("DELETE FROM authors");
        underTest = new AuthorJDBCDataAccessService(
                getJdbcTemplate(),
                authorRowMapper
        );
    }

    private Author createTestAuthor() {
        String name = FAKER.name().fullName();
        String username = FAKER.name().username();
        String email = FAKER.internet().emailAddress();
        String bio = FAKER.lorem().paragraph();
        String avatar = FAKER.internet().image();
        String coverImage = FAKER.internet().image();
        String location = FAKER.address().city();
        String website = FAKER.internet().url();
        String twitter = FAKER.name().username();
        String github = FAKER.name().username();
        String linkedin = FAKER.name().username();
        Integer followersCount = FAKER.random().nextInt(10000);
        Integer followingCount = FAKER.random().nextInt(1000);
        Integer postsCount = FAKER.random().nextInt(100);
        LocalDateTime joinedAt = FAKER.date().past(365, TimeUnit.DAYS)
                .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime createdAt = FAKER.date().past(365, TimeUnit.DAYS)
                .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime updatedAt = FAKER.date().past(30, TimeUnit.DAYS)
                .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

        Author author = new Author(name, username, email);
        author.setBio(bio);
        author.setAvatar(avatar);
        author.setCoverImage(coverImage);
        author.setLocation(location);
        author.setWebsite(website);
        author.setTwitter(twitter);
        author.setGithub(github);
        author.setLinkedin(linkedin);
        author.setFollowersCount(followersCount);
        author.setFollowingCount(followingCount);
        author.setPostsCount(postsCount);
        author.setJoinedAt(joinedAt);
        author.setCreatedAt(createdAt);
        author.setUpdatedAt(updatedAt);
        underTest.insertAuthor(author);

        return underTest.selectAllAuthors().stream()
                .filter(a -> a.getUsername().equals(username))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void itShouldSelectAllAuthors() {
        // Given
        Author author = createTestAuthor();

        // When
        List<Author> actual = underTest.selectAllAuthors();

        // Then
        assertThat(actual).isNotEmpty();
        assertThat(actual).anyMatch(a -> a.getUsername().equals(author.getUsername()));
    }

    @Test
    void itShouldSelectAuthorById() {
        // Given
        Author author = createTestAuthor();

        // When
        Optional<Author> actual = underTest.selectAuthorById(author.getId());

        // Then
        assertThat(actual).isPresent().hasValueSatisfying(a -> {
            assertThat(a.getId()).isEqualTo(author.getId());
            assertThat(a.getUsername()).isEqualTo(author.getUsername());
            assertThat(a.getEmail()).isEqualTo(author.getEmail());
        });
    }

    @Test
    void itShouldSelectAuthorByUsername() {
        // Given
        Author author = createTestAuthor();

        // When
        Optional<Author> actual = underTest.selectAuthorByUsername(author.getUsername());

        // Then
        assertThat(actual).isPresent().hasValueSatisfying(a -> {
            assertThat(a.getUsername()).isEqualTo(author.getUsername());
        });
    }

    @Test
    void itShouldReturnEmptyWhenSelectAuthorById() {
        // Given
        Long invalidAuthorId = -1L;

        // When
        Optional<Author> actual = underTest.selectAuthorById(invalidAuthorId);

        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void itShouldInsertAuthor() {
        // Given
        String name = FAKER.name().fullName();
        String username = FAKER.name().username();
        String email = FAKER.internet().emailAddress();
        Author author = new Author(name, username, email);

        // When
        underTest.insertAuthor(author);

        // Then
        Optional<Author> actual = underTest.selectAuthorByUsername(username);
        assertThat(actual).isPresent().hasValueSatisfying(a -> {
            assertThat(a.getName()).isEqualTo(name);
            assertThat(a.getUsername()).isEqualTo(username);
            assertThat(a.getEmail()).isEqualTo(email);
        });
    }

    @Test
    void itShouldExistsAuthorWithUsername() {
        // Given
        Author author = createTestAuthor();

        // When
        boolean exists = underTest.existsAuthorWithUsername(author.getUsername());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void itShouldExistsAuthorWithUsernameReturnFalseWhenDoesNotExist() {
        // Given
        String username = FAKER.name().username();

        // When
        boolean exists = underTest.existsAuthorWithUsername(username);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void itShouldExistsAuthorWithEmail() {
        // Given
        Author author = createTestAuthor();

        // When
        boolean exists = underTest.existsAuthorWithEmail(author.getEmail());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void itShouldExistsAuthorWithEmailReturnFalseWhenDoesNotExist() {
        // Given
        String email = FAKER.internet().emailAddress();

        // When
        boolean exists = underTest.existsAuthorWithEmail(email);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void itShouldExistsAuthorById() {
        // Given
        Author author = createTestAuthor();

        // When
        boolean exists = underTest.existsAuthorById(author.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void itShouldExistsAuthorByIdWillReturnFalseWhenAuthorIdNotPresent() {
        // Given
        Long authorId = -1L;

        // When
        boolean exists = underTest.existsAuthorById(authorId);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void itShouldDeleteAuthorById() {
        // Given
        Author author = createTestAuthor();

        // When
        underTest.deleteAuthorById(author.getId());

        // Then
        Optional<Author> deletedAuthor = underTest.selectAuthorById(author.getId());
        assertThat(deletedAuthor).isEmpty();
    }

    @Test
    void itShouldUpdateAuthorName() {
        // Given
        Author author = createTestAuthor();
        String newName = FAKER.name().fullName();

        Author update = new Author();
        update.setId(author.getId());
        update.setName(newName);

        // When
        underTest.updateAuthor(update);

        // Then
        Optional<Author> actual = underTest.selectAuthorById(author.getId());
        assertThat(actual).isPresent().hasValueSatisfying(a -> {
            assertThat(a.getId()).isEqualTo(author.getId());
            assertThat(a.getName()).isEqualTo(newName);
        });
    }

    @Test
    void itShouldUpdateAuthorEmail() {
        // Given
        Author author = createTestAuthor();
        String newEmail = FAKER.internet().emailAddress();

        Author update = new Author();
        update.setId(author.getId());
        update.setEmail(newEmail);

        // When
        underTest.updateAuthor(update);

        // Then
        Optional<Author> actual = underTest.selectAuthorById(author.getId());
        assertThat(actual).isPresent().hasValueSatisfying(a -> {
            assertThat(a.getId()).isEqualTo(author.getId());
            assertThat(a.getEmail()).isEqualTo(newEmail);
        });
    }

    @Test
    void itShouldNotUpdateWhenNothingToUpdate() {
        // Given
        Author author = createTestAuthor();

        Author update = new Author();
        update.setId(author.getId());

        // When
        underTest.updateAuthor(update);

        // Then
        Optional<Author> actual = underTest.selectAuthorById(author.getId());
        assertThat(actual).isPresent().hasValueSatisfying(a -> {
            assertThat(a.getId()).isEqualTo(author.getId());
            assertThat(a.getName()).isEqualTo(author.getName());
            assertThat(a.getEmail()).isEqualTo(author.getEmail());
        });
    }
}
