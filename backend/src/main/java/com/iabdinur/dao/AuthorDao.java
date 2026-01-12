package com.iabdinur.dao;

import com.iabdinur.model.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorDao {
    List<Author> selectAllAuthors();
    Optional<Author> selectAuthorById(Long authorId);
    Optional<Author> selectAuthorByUsername(String username);
    void insertAuthor(Author author);
    boolean existsAuthorWithUsername(String username);
    boolean existsAuthorWithEmail(String email);
    boolean existsAuthorById(Long authorId);
    void deleteAuthorById(Long authorId);
    void updateAuthor(Author update);
}
