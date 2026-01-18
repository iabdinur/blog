package com.iabdinur.rowmapper;

import com.github.javafaker.Faker;
import com.iabdinur.model.Author;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthorRowMapperTest {

    private Faker FAKER = new Faker();

    private Author createTestAuthor() {
        Long authorId = FAKER.random().nextLong();
        String name = FAKER.name().fullName();
        String username = FAKER.name().username();
        String email = FAKER.internet().emailAddress();
        String bio = FAKER.lorem().paragraph();
        String avatar = FAKER.internet().image();
        String coverImage = FAKER.internet().image();
        String location = FAKER.address().city();
        String website = FAKER.internet().url();
        String github = FAKER.name().username();
        String linkedin = FAKER.name().username();
        Integer followersCount = FAKER.random().nextInt(10000);
        Integer postsCount = FAKER.random().nextInt(100);
        LocalDateTime joinedAt = FAKER.date().past(365, TimeUnit.DAYS).toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime createdAt = FAKER.date().past(365, TimeUnit.DAYS).toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime updatedAt = FAKER.date().past(30, TimeUnit.DAYS).toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

        Author author = new Author(authorId, name, username, email, joinedAt, createdAt, updatedAt);
        author.setBio(bio);
        author.setAvatar(avatar);
        author.setCoverImage(coverImage);
        author.setLocation(location);
        author.setWebsite(website);
        author.setGithub(github);
        author.setLinkedin(linkedin);
        author.setFollowersCount(followersCount);
        author.setPostsCount(postsCount);
        return author;
    }

    @Test
    void itShouldMapRow() throws SQLException {
        // Given
        Author author = createTestAuthor();
        Long expectedId = author.getId();
        String expectedName = author.getName();
        String expectedUsername = author.getUsername();
        String expectedEmail = author.getEmail();
        String expectedBio = author.getBio();
        String expectedAvatar = author.getAvatar();
        String expectedCoverImage = author.getCoverImage();
        String expectedLocation = author.getLocation();
        String expectedWebsite = author.getWebsite();
        String expectedGithub = author.getGithub();
        String expectedLinkedin = author.getLinkedin();
        Integer expectedFollowersCount = author.getFollowersCount();
        Integer expectedPostsCount = author.getPostsCount();
        LocalDateTime expectedJoinedAt = author.getJoinedAt();
        LocalDateTime expectedCreatedAt = author.getCreatedAt();
        LocalDateTime expectedUpdatedAt = author.getUpdatedAt();

        AuthorRowMapper authorRowMapper = new AuthorRowMapper();
        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.getLong("id")).thenReturn(expectedId);
        when(resultSet.getString("name")).thenReturn(expectedName);
        when(resultSet.getString("username")).thenReturn(expectedUsername);
        when(resultSet.getString("email")).thenReturn(expectedEmail);
        when(resultSet.getString("bio")).thenReturn(expectedBio);
        when(resultSet.getString("avatar")).thenReturn(expectedAvatar);
        when(resultSet.getString("cover_image")).thenReturn(expectedCoverImage);
        when(resultSet.getString("location")).thenReturn(expectedLocation);
        when(resultSet.getString("website")).thenReturn(expectedWebsite);
        when(resultSet.getString("github")).thenReturn(expectedGithub);
        when(resultSet.getString("linkedin")).thenReturn(expectedLinkedin);
        when(resultSet.getInt("followers_count")).thenReturn(expectedFollowersCount);
        when(resultSet.getInt("posts_count")).thenReturn(expectedPostsCount);
        when(resultSet.getTimestamp("joined_at")).thenReturn(Timestamp.valueOf(expectedJoinedAt));
        when(resultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(expectedCreatedAt));
        when(resultSet.getTimestamp("updated_at")).thenReturn(Timestamp.valueOf(expectedUpdatedAt));

        // When
        Author actual = authorRowMapper.mapRow(resultSet, 1);

        // Then
        assertThat(actual.getId()).isEqualTo(expectedId);
        assertThat(actual.getName()).isEqualTo(expectedName);
        assertThat(actual.getUsername()).isEqualTo(expectedUsername);
        assertThat(actual.getEmail()).isEqualTo(expectedEmail);
        assertThat(actual.getBio()).isEqualTo(expectedBio);
        assertThat(actual.getAvatar()).isEqualTo(expectedAvatar);
        assertThat(actual.getCoverImage()).isEqualTo(expectedCoverImage);
        assertThat(actual.getLocation()).isEqualTo(expectedLocation);
        assertThat(actual.getWebsite()).isEqualTo(expectedWebsite);
        assertThat(actual.getGithub()).isEqualTo(expectedGithub);
        assertThat(actual.getLinkedin()).isEqualTo(expectedLinkedin);
        assertThat(actual.getFollowersCount()).isEqualTo(expectedFollowersCount);
        assertThat(actual.getPostsCount()).isEqualTo(expectedPostsCount);
        assertThat(actual.getJoinedAt()).isEqualTo(expectedJoinedAt);
        assertThat(actual.getCreatedAt()).isEqualTo(expectedCreatedAt);
        assertThat(actual.getUpdatedAt()).isEqualTo(expectedUpdatedAt);
    }
}
