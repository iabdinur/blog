package com.iabdinur.service;

import com.github.javafaker.Faker;
import com.iabdinur.dao.AuthorDao;
import com.iabdinur.dao.CommentDao;
import com.iabdinur.dao.PostDao;
import com.iabdinur.dto.CommentDTO;
import com.iabdinur.model.Author;
import com.iabdinur.model.Comment;
import com.iabdinur.model.Post;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    private CommentService underTest;
    private AutoCloseable autoCloseable;

    @Mock
    private CommentDao commentDao;
    @Mock
    private AuthorDao authorDao;
    @Mock
    private PostDao postDao;
    @Mock
    private JdbcTemplate jdbcTemplate;

    private final Faker FAKER = new Faker();

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CommentService(commentDao, authorDao, postDao, jdbcTemplate);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    private Post createTestPost() {
        Post post = new Post();
        post.setId(FAKER.random().nextLong());
        post.setTitle(FAKER.lorem().sentence());
        post.setSlug(String.join("-", FAKER.lorem().words(3)).toLowerCase());
        post.setIsPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return post;
    }

    private Author createTestAuthor() {
        Author author = new Author();
        author.setId(FAKER.random().nextLong());
        author.setName(FAKER.name().fullName());
        author.setUsername(FAKER.name().username());
        author.setEmail(FAKER.internet().emailAddress());
        return author;
    }

    @Test
    void itShouldGetCommentsByPostSlug() {
        // Given
        Post post = createTestPost();
        List<Comment> comments = new ArrayList<>();
        Comment comment = new Comment();
        comment.setId(FAKER.random().nextLong());
        comment.setContent(FAKER.lorem().sentence());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setLikes(0);
        comments.add(comment);
        
        when(postDao.selectPublishedPostBySlug(post.getSlug())).thenReturn(Optional.of(post));
        when(commentDao.selectCommentsByPostId(post.getId())).thenReturn(comments);
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), anyLong())).thenReturn(post.getId(), post.getId());

        // When
        List<CommentDTO> result = underTest.getCommentsByPostSlug(post.getSlug());

        // Then
        verify(postDao).selectPublishedPostBySlug(post.getSlug());
        verify(commentDao).selectCommentsByPostId(post.getId());
        assertThat(result).isNotEmpty();
    }

    @Test
    void itShouldReturnEmptyListWhenPostNotFound() {
        // Given
        String slug = String.join("-", FAKER.lorem().words(3)).toLowerCase();
        when(postDao.selectPublishedPostBySlug(slug)).thenReturn(Optional.empty());

        // When
        List<CommentDTO> result = underTest.getCommentsByPostSlug(slug);

        // Then
        verify(postDao).selectPublishedPostBySlug(slug);
        verify(commentDao, never()).selectCommentsByPostId(anyLong());
        assertThat(result).isEmpty();
    }

    @Test
    void itShouldCreateComment() {
        // Given
        Post post = createTestPost();
        Author author = createTestAuthor();
        String content = FAKER.lorem().sentence();
        
        when(postDao.selectPublishedPostBySlug(post.getSlug())).thenReturn(Optional.of(post));
        when(authorDao.selectAuthorById(author.getId())).thenReturn(Optional.of(author));
        when(postDao.selectPostById(post.getId())).thenReturn(Optional.of(post));
        // Mock loadCommentRelationships calls: post_id, author_id, parent_id (null)
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), anyLong())).thenReturn(post.getId(), author.getId(), null);
        when(jdbcTemplate.update(anyString(), anyLong())).thenReturn(1);
        
        // Mock insertComment to set an ID on the comment
        doAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            comment.setId(FAKER.random().nextLong());
            return null;
        }).when(commentDao).insertComment(any(Comment.class));

        // When
        CommentDTO result = underTest.createComment(post.getSlug(), content, author.getId(), null);

        // Then
        ArgumentCaptor<Comment> commentArgumentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentDao).insertComment(commentArgumentCaptor.capture());
        Comment capturedComment = commentArgumentCaptor.getValue();
        
        assertEquals(content, capturedComment.getContent());
        assertEquals(post.getId(), capturedComment.getPost().getId());
        assertEquals(author.getId(), capturedComment.getAuthor().getId());
        verify(jdbcTemplate).update(anyString(), eq(post.getId()));
        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
    }

    @Test
    void itShouldCreateCommentWithParent() {
        // Given
        Post post = createTestPost();
        Author author = createTestAuthor();
        Comment parent = new Comment();
        parent.setId(FAKER.random().nextLong());
        
        when(postDao.selectPublishedPostBySlug(post.getSlug())).thenReturn(Optional.of(post));
        when(authorDao.selectAuthorById(author.getId())).thenReturn(Optional.of(author));
        when(commentDao.selectCommentById(parent.getId())).thenReturn(Optional.of(parent));
        when(postDao.selectPostById(post.getId())).thenReturn(Optional.of(post));
        // Mock loadCommentRelationships calls: post_id, author_id, parent_id
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), anyLong())).thenReturn(post.getId(), author.getId(), parent.getId());
        when(jdbcTemplate.update(anyString(), anyLong())).thenReturn(1);
        
        // Mock insertComment to set an ID on the comment
        doAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            comment.setId(FAKER.random().nextLong());
            return null;
        }).when(commentDao).insertComment(any(Comment.class));

        // When
        CommentDTO result = underTest.createComment(post.getSlug(), FAKER.lorem().sentence(), author.getId(), parent.getId());

        // Then
        ArgumentCaptor<Comment> commentArgumentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentDao).insertComment(commentArgumentCaptor.capture());
        Comment capturedComment = commentArgumentCaptor.getValue();
        
        assertEquals(parent.getId(), capturedComment.getParent().getId());
        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
    }

    @Test
    void itShouldThrowWhenPostNotFoundWhileCreatingComment() {
        // Given
        String slug = String.join("-", FAKER.lorem().words(3)).toLowerCase();
        Long authorId = FAKER.random().nextLong();
        when(postDao.selectPublishedPostBySlug(slug)).thenReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> underTest.createComment(slug, FAKER.lorem().sentence(), authorId, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Post not found");

        // Then
        verify(commentDao, never()).insertComment(any());
    }

    @Test
    void itShouldThrowWhenAuthorNotFoundWhileCreatingComment() {
        // Given
        Post post = createTestPost();
        Long authorId = FAKER.random().nextLong();
        
        when(postDao.selectPublishedPostBySlug(post.getSlug())).thenReturn(Optional.of(post));
        when(authorDao.selectAuthorById(authorId)).thenReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> underTest.createComment(post.getSlug(), FAKER.lorem().sentence(), authorId, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Author not found");

        // Then
        verify(commentDao, never()).insertComment(any());
    }

    @Test
    void itShouldIncrementLikes() {
        // Given
        Long commentId = FAKER.random().nextLong();
        when(commentDao.existsCommentById(commentId)).thenReturn(true);

        // When
        underTest.incrementLikes(commentId);

        // Then
        verify(commentDao).existsCommentById(commentId);
        verify(commentDao).incrementLikes(commentId);
    }

    @Test
    void itShouldThrowWhenIncrementingLikesForNonExistentComment() {
        // Given
        Long commentId = FAKER.random().nextLong();
        when(commentDao.existsCommentById(commentId)).thenReturn(false);

        // When
        assertThatThrownBy(() -> underTest.incrementLikes(commentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Comment not found");

        // Then
        verify(commentDao, never()).incrementLikes(anyLong());
    }

    @Test
    void itShouldDeleteComment() {
        // Given
        Long commentId = FAKER.random().nextLong();
        Long postId = FAKER.random().nextLong();
        Comment comment = new Comment();
        comment.setId(commentId);
        Post post = new Post();
        post.setId(postId);
        comment.setPost(post);
        
        when(commentDao.selectCommentById(commentId)).thenReturn(Optional.of(comment));
        when(jdbcTemplate.update(anyString(), anyLong())).thenReturn(1);

        // When
        underTest.deleteComment(commentId);

        // Then
        verify(commentDao).selectCommentById(commentId);
        verify(commentDao).deleteCommentById(commentId);
        verify(jdbcTemplate).update(anyString(), eq(postId));
    }

    @Test
    void itShouldThrowWhenDeletingNonExistentComment() {
        // Given
        Long commentId = FAKER.random().nextLong();
        when(commentDao.selectCommentById(commentId)).thenReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> underTest.deleteComment(commentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Comment not found");

        // Then
        verify(commentDao, never()).deleteCommentById(anyLong());
    }
}
