package com.iabdinur.service;

import com.github.javafaker.Faker;
import com.iabdinur.dao.AuthorDao;
import com.iabdinur.dao.PostDao;
import com.iabdinur.dao.TagDao;
import com.iabdinur.dto.CreatePostRequest;
import com.iabdinur.dto.PostDTO;
import com.iabdinur.dto.PostListResponse;
import com.iabdinur.model.Author;
import com.iabdinur.model.Post;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    private PostService underTest;
    private AutoCloseable autoCloseable;

    @Mock
    private PostDao postDao;
    @Mock
    private AuthorDao authorDao;
    @Mock
    private TagDao tagDao;
    @Mock
    private JdbcTemplate jdbcTemplate;

    private final Faker FAKER = new Faker();

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new PostService(postDao, authorDao, tagDao, jdbcTemplate);
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
        post.setContent(FAKER.lorem().paragraph());
        post.setExcerpt(FAKER.lorem().sentence());
        post.setIsPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        post.setViews(0L);
        post.setLikes(0L);
        post.setCommentsCount(0);
        post.setReadingTime(5);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        return post;
    }

    private Author createTestAuthor() {
        Author author = new Author();
        author.setId(FAKER.random().nextLong());
        author.setName(FAKER.name().fullName());
        author.setUsername(FAKER.name().username());
        author.setEmail(FAKER.internet().emailAddress());
        author.setCreatedAt(LocalDateTime.now());
        author.setUpdatedAt(LocalDateTime.now());
        return author;
    }

    @Test
    void itShouldGetAllPosts() {
        // Given
        List<Post> posts = new ArrayList<>();
        Post post = createTestPost();
        posts.add(post);

        when(postDao.selectPublishedPosts(anyInt(), anyInt())).thenReturn(posts);
        when(postDao.countPublishedPosts()).thenReturn(1L);
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), anyLong())).thenReturn(1L);
        when(jdbcTemplate.query(anyString(), isA(org.springframework.jdbc.core.RowMapper.class), anyLong())).thenReturn(new ArrayList<>());

        // When
        PostListResponse result = underTest.getAllPosts("latest", 1, 10, null, null, null);

        // Then
        verify(postDao).selectPublishedPosts(10, 0);
        verify(postDao).countPublishedPosts();
        assertThat(result.posts()).isNotEmpty();
    }

    @Test
    void itShouldGetAllPostsByTag() {
        // Given
        String tagSlug = String.join("-", FAKER.lorem().words(2)).toLowerCase();
        List<Post> posts = new ArrayList<>();
        when(postDao.selectPostsByTagSlug(tagSlug, 10, 0)).thenReturn(posts);
        when(postDao.countPostsByTagSlug(tagSlug)).thenReturn(0L);

        // When
        PostListResponse result = underTest.getAllPosts("latest", 1, 10, tagSlug, null, null);

        // Then
        verify(postDao).selectPostsByTagSlug(tagSlug, 10, 0);
        verify(postDao).countPostsByTagSlug(tagSlug);
    }

    @Test
    void itShouldGetAllPostsByAuthor() {
        // Given
        Author author = createTestAuthor();
        List<Post> posts = new ArrayList<>();
        
        when(authorDao.selectAuthorByUsername(author.getUsername())).thenReturn(Optional.of(author));
        when(postDao.selectPostsByAuthorId(author.getId(), 10, 0)).thenReturn(posts);
        when(postDao.countPostsByAuthorId(author.getId())).thenReturn(0L);

        // When
        PostListResponse result = underTest.getAllPosts("latest", 1, 10, null, author.getUsername(), null);

        // Then
        verify(authorDao).selectAuthorByUsername(author.getUsername());
        verify(postDao).selectPostsByAuthorId(author.getId(), 10, 0);
    }

    @Test
    void itShouldReturnEmptyWhenAuthorNotFound() {
        // Given
        String username = FAKER.name().username();
        when(authorDao.selectAuthorByUsername(username)).thenReturn(Optional.empty());

        // When
        PostListResponse result = underTest.getAllPosts("latest", 1, 10, null, username, null);

        // Then
        verify(authorDao).selectAuthorByUsername(username);
        verify(postDao, never()).selectPostsByAuthorId(anyLong(), anyInt(), anyInt());
        assertThat(result.posts()).isEmpty();
        assertEquals(0, result.total());
    }

    @Test
    void itShouldGetPostBySlug() {
        // Given
        Post post = createTestPost();
        
        when(postDao.selectPublishedPostBySlug(post.getSlug())).thenReturn(Optional.of(post));
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), anyLong())).thenReturn(post.getId());
        when(jdbcTemplate.query(anyString(), isA(org.springframework.jdbc.core.RowMapper.class), anyLong())).thenReturn(new ArrayList<>());

        // When
        Optional<PostDTO> result = underTest.getPostBySlug(post.getSlug());

        // Then
        verify(postDao).selectPublishedPostBySlug(post.getSlug());
        assertThat(result).isPresent();
        assertEquals(post.getSlug(), result.get().slug());
    }

    @Test
    void itShouldReturnEmptyWhenPostNotFound() {
        // Given
        String slug = String.join("-", FAKER.lorem().words(3)).toLowerCase();
        when(postDao.selectPublishedPostBySlug(slug)).thenReturn(Optional.empty());

        // When
        Optional<PostDTO> result = underTest.getPostBySlug(slug);

        // Then
        verify(postDao).selectPublishedPostBySlug(slug);
        assertThat(result).isEmpty();
    }

    @Test
    void itShouldSearchPosts() {
        // Given
        String query = FAKER.lorem().word();
        List<Post> posts = new ArrayList<>();
        when(jdbcTemplate.query(anyString(), isA(org.springframework.jdbc.core.RowMapper.class), anyString(), anyString(), anyString(), anyInt(), anyInt())).thenReturn(posts);
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), anyString(), anyString(), anyString())).thenReturn(0L);

        // When
        PostListResponse result = underTest.searchPosts(query, 1, 10);

        // Then
        verify(jdbcTemplate).query(anyString(), isA(org.springframework.jdbc.core.RowMapper.class), eq(query), eq(query), eq(query), eq(10), eq(0));
        verify(jdbcTemplate).queryForObject(anyString(), eq(Long.class), eq(query), eq(query), eq(query));
        assertThat(result).isNotNull();
        assertThat(result.total()).isEqualTo(0);
    }

    @Test
    void itShouldCreatePost() {
        // Given
        Author author = createTestAuthor();
        Tag tag = new Tag();
        tag.setId(FAKER.random().nextLong());
        
        CreatePostRequest request = new CreatePostRequest(
                FAKER.lorem().sentence(),
                String.join("-", FAKER.lorem().words(3)).toLowerCase(),
                FAKER.lorem().paragraph(),
                FAKER.lorem().sentence(),
                null, // coverImage
                null, // contentImage
                author.getId().toString(),
                List.of(tag.getId().toString()),
                true,
                5,
                null // scheduledAt
        );

        when(authorDao.selectAuthorById(author.getId())).thenReturn(Optional.of(author));
        when(tagDao.selectTagById(tag.getId())).thenReturn(Optional.of(tag));
        
        // Mock insertPost to set an ID on the post
        doAnswer(invocation -> {
            Post post = invocation.getArgument(0);
            post.setId(FAKER.random().nextLong());
            return null;
        }).when(postDao).insertPost(any(Post.class));
        
        when(jdbcTemplate.update(anyString(), anyLong(), anyLong())).thenReturn(1);
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), anyLong())).thenReturn(author.getId());
        when(jdbcTemplate.query(anyString(), isA(org.springframework.jdbc.core.RowMapper.class), anyLong())).thenReturn(new ArrayList<>());

        // When
        PostDTO result = underTest.createPost(request);

        // Then
        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postDao).insertPost(postArgumentCaptor.capture());
        Post capturedPost = postArgumentCaptor.getValue();
        
        assertEquals(request.title(), capturedPost.getTitle());
        assertEquals(request.slug(), capturedPost.getSlug());
        assertThat(capturedPost.getIsPublished()).isTrue();
        verify(jdbcTemplate).update(anyString(), eq(capturedPost.getId()), eq(tag.getId()));
    }

    @Test
    void itShouldThrowWhenAuthorNotFoundWhileCreatingPost() {
        // Given
        Long authorId = FAKER.random().nextLong();
        CreatePostRequest request = new CreatePostRequest(
                FAKER.lorem().sentence(),
                String.join("-", FAKER.lorem().words(3)).toLowerCase(),
                FAKER.lorem().paragraph(),
                FAKER.lorem().sentence(),
                null, // coverImage
                null, // contentImage
                authorId.toString(),
                new ArrayList<>(),
                true,
                5,
                null // scheduledAt
        );

        when(authorDao.selectAuthorById(authorId)).thenReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> underTest.createPost(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Author not found");

        // Then
        verify(postDao, never()).insertPost(any());
    }

    @Test
    void itShouldUpdatePost() {
        // Given
        Post post = createTestPost();
        String newTitle = "Updated " + FAKER.lorem().sentence();
        
        CreatePostRequest request = new CreatePostRequest(
                newTitle,
                post.getSlug(),
                FAKER.lorem().paragraph(),
                FAKER.lorem().sentence(),
                null, // coverImage
                null, // contentImage
                "1",
                new ArrayList<>(),
                true,
                10,
                null // scheduledAt
        );

        when(postDao.selectPostBySlug(post.getSlug())).thenReturn(Optional.of(post));
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), anyLong())).thenReturn(1L);
        when(jdbcTemplate.query(anyString(), isA(org.springframework.jdbc.core.RowMapper.class), anyLong())).thenReturn(new ArrayList<>());

        // When
        Optional<PostDTO> result = underTest.updatePost(post.getSlug(), request);

        // Then
        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postDao).updatePost(postArgumentCaptor.capture());
        Post capturedPost = postArgumentCaptor.getValue();
        
        assertEquals(newTitle, capturedPost.getTitle());
        assertEquals(10, capturedPost.getReadingTime());
    }

    @Test
    void itShouldReturnEmptyWhenUpdatingNonExistentPost() {
        // Given
        String slug = String.join("-", FAKER.lorem().words(3)).toLowerCase();
        CreatePostRequest request = new CreatePostRequest(
                FAKER.lorem().sentence(),
                slug,
                FAKER.lorem().paragraph(),
                FAKER.lorem().sentence(),
                null, // coverImage
                null, // contentImage
                "1",
                new ArrayList<>(),
                true,
                5,
                null // scheduledAt
        );

        when(postDao.selectPostBySlug(slug)).thenReturn(Optional.empty());

        // When
        Optional<PostDTO> result = underTest.updatePost(slug, request);

        // Then
        verify(postDao).selectPostBySlug(slug);
        verify(postDao, never()).updatePost(any());
        assertThat(result).isEmpty();
    }

    @Test
    void itShouldDeletePost() {
        // Given
        Post post = createTestPost();
        when(postDao.selectPostBySlug(post.getSlug())).thenReturn(Optional.of(post));

        // When
        boolean result = underTest.deletePost(post.getSlug());

        // Then
        verify(postDao).selectPostBySlug(post.getSlug());
        verify(postDao).deletePostById(post.getId());
        assertThat(result).isTrue();
    }

    @Test
    void itShouldReturnFalseWhenDeletingNonExistentPost() {
        // Given
        String slug = String.join("-", FAKER.lorem().words(3)).toLowerCase();
        when(postDao.selectPostBySlug(slug)).thenReturn(Optional.empty());

        // When
        boolean result = underTest.deletePost(slug);

        // Then
        verify(postDao).selectPostBySlug(slug);
        verify(postDao, never()).deletePostById(anyLong());
        assertThat(result).isFalse();
    }

    @Test
    void itShouldIncrementViews() {
        // Given
        Post post = createTestPost();
        when(postDao.selectPostBySlug(post.getSlug())).thenReturn(Optional.of(post));

        // When
        underTest.incrementViews(post.getSlug());

        // Then
        verify(postDao).selectPostBySlug(post.getSlug());
        verify(postDao).incrementViews(post.getId());
    }

    @Test
    void itShouldThrowWhenIncrementingViewsForNonExistentPost() {
        // Given
        String slug = String.join("-", FAKER.lorem().words(3)).toLowerCase();
        when(postDao.selectPostBySlug(slug)).thenReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> underTest.incrementViews(slug))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Post not found");

        // Then
        verify(postDao, never()).incrementViews(anyLong());
    }

    @Test
    void itShouldIncrementLikes() {
        // Given
        Post post = createTestPost();
        when(postDao.selectPostBySlug(post.getSlug())).thenReturn(Optional.of(post));

        // When
        underTest.incrementLikes(post.getSlug());

        // Then
        verify(postDao).selectPostBySlug(post.getSlug());
        verify(postDao).incrementLikes(post.getId());
    }

    @Test
    void itShouldGetPostBySlugForAdmin() {
        // Given
        Post post = createTestPost();
        post.setIsPublished(false);
        
        when(postDao.selectPostBySlug(post.getSlug())).thenReturn(Optional.of(post));
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), anyLong())).thenReturn(post.getId());
        when(jdbcTemplate.query(anyString(), isA(org.springframework.jdbc.core.RowMapper.class), anyLong())).thenReturn(new ArrayList<>());

        // When
        Optional<PostDTO> result = underTest.getPostBySlugForAdmin(post.getSlug());

        // Then
        verify(postDao).selectPostBySlug(post.getSlug());
        assertThat(result).isPresent();
        assertThat(result.get().isPublished()).isFalse();
    }

    @Test
    void itShouldReplaceContentImagePlaceholder() {
        // Given
        Post post = createTestPost();
        Author author = createTestAuthor();
        post.setAuthor(author);
        String contentWithPlaceholder = "Some content {{content_image}} more content";
        String contentImageUrl = FAKER.internet().image();
        post.setContent(contentWithPlaceholder);
        post.setContentImage(contentImageUrl);
        
        when(postDao.selectPublishedPostBySlug(post.getSlug())).thenReturn(Optional.of(post));
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), anyLong())).thenReturn(author.getId());
        when(jdbcTemplate.query(anyString(), isA(org.springframework.jdbc.core.RowMapper.class), anyLong())).thenReturn(new ArrayList<>());

        // When
        Optional<PostDTO> result = underTest.getPostBySlug(post.getSlug());

        // Then
        assertThat(result).isPresent();
        String processedContent = result.get().content();
        assertThat(processedContent).contains("![Content Image](" + contentImageUrl + ")");
        assertThat(processedContent).doesNotContain("{{content_image}}");
    }

    @Test
    void itShouldNotReplacePlaceholderWhenContentImageIsNull() {
        // Given
        Post post = createTestPost();
        Author author = createTestAuthor();
        post.setAuthor(author);
        String contentWithPlaceholder = "Some content {{content_image}} more content";
        post.setContent(contentWithPlaceholder);
        post.setContentImage(null);
        
        when(postDao.selectPublishedPostBySlug(post.getSlug())).thenReturn(Optional.of(post));
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), anyLong())).thenReturn(author.getId());
        when(jdbcTemplate.query(anyString(), isA(org.springframework.jdbc.core.RowMapper.class), anyLong())).thenReturn(new ArrayList<>());

        // When
        Optional<PostDTO> result = underTest.getPostBySlug(post.getSlug());

        // Then
        assertThat(result).isPresent();
        String processedContent = result.get().content();
        assertThat(processedContent).contains("{{content_image}}");
        assertThat(processedContent).doesNotContain("![Content Image]");
    }
}
