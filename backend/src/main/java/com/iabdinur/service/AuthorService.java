package com.iabdinur.service;

import com.iabdinur.dao.AuthorDao;
import com.iabdinur.dto.AuthorDTO;
import com.iabdinur.dto.CreateAuthorRequest;
import com.iabdinur.model.Author;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AuthorService {
    private final AuthorDao authorDao;
    private final JdbcTemplate jdbcTemplate;

    public AuthorService(AuthorDao authorDao, JdbcTemplate jdbcTemplate) {
        this.authorDao = authorDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<AuthorDTO> getAuthorById(String id) {
        try {
            Long authorId = Long.parseLong(id);
            return authorDao.selectAuthorById(authorId)
                .map(AuthorDTO::fromEntity);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public Optional<AuthorDTO> getAuthorByUsername(String username) {
        return authorDao.selectAuthorByUsername(username)
            .map(AuthorDTO::fromEntity);
    }

    public Optional<AuthorDTO> getAuthorByEmail(String email) {
        var sql = """
                SELECT id, name, username, email, bio, avatar, cover_image, location, website,
                       github, linkedin, followers_count, posts_count,
                       joined_at, created_at, updated_at
                FROM authors
                WHERE email = ?
                """;
        List<Author> authors = jdbcTemplate.query(sql,
            (rs, rowNum) -> {
                java.sql.Timestamp joinedAtTimestamp = rs.getTimestamp("joined_at");
                java.sql.Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
                java.sql.Timestamp updatedAtTimestamp = rs.getTimestamp("updated_at");
                
                Author author = new Author(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("username"),
                    rs.getString("email"),
                    joinedAtTimestamp != null ? joinedAtTimestamp.toLocalDateTime() : java.time.LocalDateTime.now(),
                    createdAtTimestamp != null ? createdAtTimestamp.toLocalDateTime() : java.time.LocalDateTime.now(),
                    updatedAtTimestamp != null ? updatedAtTimestamp.toLocalDateTime() : java.time.LocalDateTime.now()
                );
                author.setBio(rs.getString("bio"));
                author.setAvatar(rs.getString("avatar"));
                author.setCoverImage(rs.getString("cover_image"));
                author.setLocation(rs.getString("location"));
                author.setWebsite(rs.getString("website"));
                author.setGithub(rs.getString("github"));
                author.setLinkedin(rs.getString("linkedin"));
                author.setFollowersCount(rs.getInt("followers_count"));
                author.setPostsCount(rs.getInt("posts_count"));
                return author;
            },
            email);
        
        return authors.stream()
            .findFirst()
            .map(AuthorDTO::fromEntity);
    }

    public List<AuthorDTO> searchAuthors(String query) {
        // Manual search implementation
        var sql = """
                SELECT id, name, username, email, bio, avatar, cover_image, location, website,
                       github, linkedin, followers_count, posts_count,
                       joined_at, created_at, updated_at
                FROM authors
                WHERE LOWER(name) LIKE LOWER(CONCAT('%', ?, '%'))
                   OR LOWER(username) LIKE LOWER(CONCAT('%', ?, '%'))
                   OR LOWER(bio) LIKE LOWER(CONCAT('%', ?, '%'))
                ORDER BY name
                """;
        List<Author> authors = jdbcTemplate.query(sql, 
            (rs, rowNum) -> {
                java.sql.Timestamp joinedAtTimestamp = rs.getTimestamp("joined_at");
                java.sql.Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
                java.sql.Timestamp updatedAtTimestamp = rs.getTimestamp("updated_at");
                
                Author author = new Author(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("username"),
                    rs.getString("email"),
                    joinedAtTimestamp != null ? joinedAtTimestamp.toLocalDateTime() : java.time.LocalDateTime.now(),
                    createdAtTimestamp != null ? createdAtTimestamp.toLocalDateTime() : java.time.LocalDateTime.now(),
                    updatedAtTimestamp != null ? updatedAtTimestamp.toLocalDateTime() : java.time.LocalDateTime.now()
                );
                author.setBio(rs.getString("bio"));
                author.setAvatar(rs.getString("avatar"));
                author.setCoverImage(rs.getString("cover_image"));
                author.setLocation(rs.getString("location"));
                author.setWebsite(rs.getString("website"));
                author.setGithub(rs.getString("github"));
                author.setLinkedin(rs.getString("linkedin"));
                author.setFollowersCount(rs.getInt("followers_count"));
                author.setPostsCount(rs.getInt("posts_count"));
                return author;
            },
            query, query, query);
        return authors.stream()
            .map(AuthorDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public List<AuthorDTO> getAllAuthors() {
        List<Author> authors = authorDao.selectAllAuthors();
        return authors.stream()
            .map(AuthorDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional
    public AuthorDTO createAuthor(CreateAuthorRequest request) {
        Author author = new Author();
        author.setName(request.name());
        author.setUsername(request.username());
        author.setEmail(request.email());
        author.setBio(request.bio());
        author.setAvatar(request.avatar());
        author.setCoverImage(request.coverImage());
        author.setLocation(request.location());
        author.setWebsite(request.website());
        author.setGithub(request.github());
        author.setLinkedin(request.linkedin());
        author.setFollowersCount(0);
        author.setPostsCount(0);
        author.setJoinedAt(LocalDateTime.now());
        author.setCreatedAt(LocalDateTime.now());
        author.setUpdatedAt(LocalDateTime.now());

        authorDao.insertAuthor(author);
        return AuthorDTO.fromEntity(author);
    }

    @Transactional
    public Optional<AuthorDTO> updateAuthor(String username, CreateAuthorRequest request) {
        Optional<Author> authorOpt = authorDao.selectAuthorByUsername(username);
        if (authorOpt.isEmpty()) {
            return Optional.empty();
        }

        Author author = authorOpt.get();
        author.setName(request.name());
        author.setUsername(request.username());
        author.setEmail(request.email());
        author.setBio(request.bio());
        author.setAvatar(request.avatar());
        author.setCoverImage(request.coverImage());
        author.setLocation(request.location());
        author.setWebsite(request.website());
        author.setGithub(request.github());
        author.setLinkedin(request.linkedin());
        author.setUpdatedAt(LocalDateTime.now());

        authorDao.updateAuthor(author);
        return Optional.of(AuthorDTO.fromEntity(author));
    }

    @Transactional
    public boolean deleteAuthor(String username) {
        Optional<Author> authorOpt = authorDao.selectAuthorByUsername(username);
        if (authorOpt.isEmpty()) {
            return false;
        }
        authorDao.deleteAuthorById(authorOpt.get().getId());
        return true;
    }

}

