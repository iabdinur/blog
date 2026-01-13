package com.iabdinur.journey;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iabdinur.AbstractTestcontainers;
import com.iabdinur.dao.UserDao;
import com.iabdinur.dao.AuthorDao;
import com.iabdinur.dao.TagDao;
import com.iabdinur.dto.*;
import com.iabdinur.model.User;
import com.iabdinur.model.Author;
import com.iabdinur.model.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.iabdinur.BlogApp.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class BlogAppIT extends AbstractTestcontainers {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthorDao authorDao;

    @Autowired
    private TagDao tagDao;

    @Autowired
    private com.iabdinur.service.UserService userService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // Clean up after each test (order matters due to foreign keys)
        jdbcTemplate.execute("DELETE FROM post_tags");
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("DELETE FROM posts");
        jdbcTemplate.execute("DELETE FROM tags");
        jdbcTemplate.execute("DELETE FROM authors");
        jdbcTemplate.execute("DELETE FROM users");
    }

    // Helper class to return both user and plain password
    private static class TestUser {
        final User user;
        final String plainPassword;
        
        TestUser(User user, String plainPassword) {
            this.user = user;
            this.plainPassword = plainPassword;
        }
    }
    
    private TestUser createTestUser() {
        String name = FAKER.name().fullName();
        String email = FAKER.internet().emailAddress();
        String password = FAKER.internet().password();
        // Use UserService to ensure password is hashed
        userService.createUser(name, email, password);
        User user = userDao.selectUserByEmail(email)
                .orElseThrow();
        return new TestUser(user, password);
    }

    private Author createTestAuthor() {
        String name = FAKER.name().fullName();
        String username = FAKER.name().username();
        String email = FAKER.internet().emailAddress();
        Author author = new Author(name, username, email);
        authorDao.insertAuthor(author);
        return authorDao.selectAllAuthors().stream()
                .filter(a -> a.getUsername().equals(username))
                .findFirst()
                .orElseThrow();
    }

    private Tag createTestTag() {
        String name = FAKER.lorem().word();
        String slug = String.join("-", FAKER.lorem().words(2)).toLowerCase();
        String description = FAKER.lorem().sentence();
        Tag tag = new Tag(name, slug);
        tag.setDescription(description);
        tagDao.insertTag(tag);
        return tagDao.selectAllTags().stream()
                .filter(t -> t.getSlug().equals(slug))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void itShouldAllowUserToLoginCreateContentAndInteractWithPosts() throws Exception {
        // Define CreateCommentRequest record for use in this test
        record CreateCommentRequest(String content, String parentId) {}

        // Ensure an author with ID 1 exists for comments (CommentController hardcodes authorId = 1L)
        // Check if author with ID 1 exists, if not create one
        try {
            jdbcTemplate.queryForObject("SELECT id FROM authors WHERE id = 1", Long.class);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            // Author with ID 1 doesn't exist, create one
            jdbcTemplate.update(
                    "INSERT INTO authors (id, name, username, email, created_at, updated_at) VALUES (1, ?, ?, ?, ?, ?)",
                    FAKER.name().fullName(),
                    FAKER.name().username(),
                    FAKER.internet().emailAddress(),
                    java.time.LocalDateTime.now(),
                    java.time.LocalDateTime.now()
            );
            // Reset sequence to continue from 2
            jdbcTemplate.execute("SELECT setval('authors_id_seq', (SELECT MAX(id) FROM authors))");
        }

        // ========== STEP 1: Login Experience ==========
        // Given - Create a user
        TestUser testUser = createTestUser();
        User user = testUser.user;
        com.iabdinur.dto.AuthenticationRequest authRequest = new com.iabdinur.dto.AuthenticationRequest(
                user.getEmail(), testUser.plainPassword);

        // When - Login using new authentication endpoint
        String loginResponseJson = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.email").value(user.getEmail()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        com.iabdinur.dto.AuthenticationResponse authResponse = objectMapper.readValue(loginResponseJson, com.iabdinur.dto.AuthenticationResponse.class);
        String authToken = authResponse.token();

        // Then - Verify login was successful
        assertThat(authToken).isNotNull();
        assertThat(authResponse.user().email()).isEqualTo(user.getEmail());

        // ========== STEP 2: Create Tag ==========
        // Given - Tag creation request
        String tagName = FAKER.lorem().word();
        String tagSlug = String.join("-", FAKER.lorem().words(2)).toLowerCase();
        String tagDescription = FAKER.lorem().sentence();
        CreateTagRequest createTagRequest = new CreateTagRequest(tagName, tagSlug, tagDescription);

        // When - Create tag
        String tagResponseJson = mockMvc.perform(post("/api/v1/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTagRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(tagName))
                .andExpect(jsonPath("$.slug").value(tagSlug))
                .andExpect(jsonPath("$.description").value(tagDescription))
                .andReturn()
                .getResponse()
                .getContentAsString();

        TagDTO createdTag = objectMapper.readValue(tagResponseJson, TagDTO.class);

        // Then - Verify tag was created
        assertThat(createdTag.name()).isEqualTo(tagName);
        assertThat(createdTag.slug()).isEqualTo(tagSlug);

        // ========== STEP 3: Create Author ==========
        // Given - Author creation request
        String authorName = FAKER.name().fullName();
        String authorUsername = FAKER.name().username();
        String authorEmail = FAKER.internet().emailAddress();
        String authorBio = FAKER.lorem().paragraph();
        CreateAuthorRequest createAuthorRequest = new CreateAuthorRequest(
                authorName,
                authorUsername,
                authorEmail,
                authorBio,
                FAKER.internet().avatar(),
                FAKER.internet().image(),
                FAKER.address().city(),
                FAKER.internet().url(),
                "https://twitter.com/" + authorUsername,
                "https://github.com/" + authorUsername,
                "https://linkedin.com/in/" + authorUsername
        );

        // When - Create author
        String authorResponseJson = mockMvc.perform(post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAuthorRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(authorName))
                .andExpect(jsonPath("$.username").value(authorUsername))
                .andExpect(jsonPath("$.email").value(authorEmail))
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuthorDTO createdAuthor = objectMapper.readValue(authorResponseJson, AuthorDTO.class);

        // Then - Verify author was created
        assertThat(createdAuthor.name()).isEqualTo(authorName);
        assertThat(createdAuthor.username()).isEqualTo(authorUsername);

        // ========== STEP 4: Write and Post New Article ==========
        // Given - Post creation request
        String postTitle = FAKER.lorem().sentence();
        String postSlug = String.join("-", FAKER.lorem().words(3)).toLowerCase();
        String postContent = FAKER.lorem().paragraph(10);
        String postExcerpt = FAKER.lorem().sentence();
        String postCoverImage = FAKER.internet().image();
        Integer readingTime = FAKER.random().nextInt(5, 30);
        CreatePostRequest createPostRequest = new CreatePostRequest(
                postTitle,
                postSlug,
                postContent,
                postExcerpt,
                postCoverImage,
                createdAuthor.id().toString(),
                List.of(createdTag.id().toString()),
                true, // isPublished
                readingTime
        );

        // When - Create post
        String postResponseJson = mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPostRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(postTitle))
                .andExpect(jsonPath("$.slug").value(postSlug))
                .andExpect(jsonPath("$.views").value(0L))
                .andExpect(jsonPath("$.likes").value(0L))
                .andExpect(jsonPath("$.commentsCount").value(0))
                .andReturn()
                .getResponse()
                .getContentAsString();

        PostDTO createdPost = objectMapper.readValue(postResponseJson, PostDTO.class);

        // Then - Verify post was created with zero initial values
        assertThat(createdPost.title()).isEqualTo(postTitle);
        assertThat(createdPost.slug()).isEqualTo(postSlug);
        assertThat(createdPost.views()).isEqualTo(0L);
        assertThat(createdPost.likes()).isEqualTo(0L);
        assertThat(createdPost.commentsCount()).isEqualTo(0);

        // ========== STEP 5: View Post and Increment Views ==========
        // When - Increment views
        mockMvc.perform(post("/api/v1/posts/{slug}/views", postSlug))
                .andExpect(status().isOk());

        // Then - Verify views were incremented
        String postAfterViewJson = mockMvc.perform(get("/api/v1/posts/{slug}", postSlug))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.views").value(1L))
                .andReturn()
                .getResponse()
                .getContentAsString();

        PostDTO postAfterView = objectMapper.readValue(postAfterViewJson, PostDTO.class);
        assertThat(postAfterView.views()).isEqualTo(1L);

        // ========== STEP 6: Like Post ==========
        // When - Like post
        mockMvc.perform(post("/api/v1/posts/{slug}/like", postSlug))
                .andExpect(status().isOk());

        // Then - Verify likes were incremented
        String postAfterLikeJson = mockMvc.perform(get("/api/v1/posts/{slug}", postSlug))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes").value(1L))
                .andReturn()
                .getResponse()
                .getContentAsString();

        PostDTO postAfterLike = objectMapper.readValue(postAfterLikeJson, PostDTO.class);
        assertThat(postAfterLike.likes()).isEqualTo(1L);

        // ========== STEP 7: Comment on Post ==========
        // Given - Comment creation request
        String commentContent = FAKER.lorem().paragraph();
        CreateCommentRequest createCommentRequest = new CreateCommentRequest(commentContent, null);

        // When - Create comment
        String commentResponseJson = mockMvc.perform(post("/api/v1/posts/{slug}/comments", postSlug)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(commentContent))
                .andReturn()
                .getResponse()
                .getContentAsString();

        CommentDTO createdComment = objectMapper.readValue(commentResponseJson, CommentDTO.class);

        // Then - Verify comment was created
        assertThat(createdComment.content()).isEqualTo(commentContent);

        // Verify comments count was incremented
        String postAfterCommentJson = mockMvc.perform(get("/api/v1/posts/{slug}", postSlug))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentsCount").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        PostDTO postAfterComment = objectMapper.readValue(postAfterCommentJson, PostDTO.class);
        assertThat(postAfterComment.commentsCount()).isEqualTo(1);

        // ========== STEP 8: Get All Comments ==========
        // When - Get comments
        String commentsJson = mockMvc.perform(get("/api/v1/posts/{slug}/comments", postSlug))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].content").value(commentContent))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<CommentDTO> comments = objectMapper.readValue(commentsJson,
                objectMapper.getTypeFactory().constructCollectionType(List.class, CommentDTO.class));

        // Then - Verify comments were retrieved
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).content()).isEqualTo(commentContent);

        // ========== STEP 9: Like Comment ==========
        // When - Like comment
        mockMvc.perform(post("/api/v1/posts/{slug}/comments/{commentId}/like",
                        postSlug, createdComment.id().toString()))
                .andExpect(status().isOk());

        // Then - Verify comment likes were incremented (by checking the comment again)
        String commentsAfterLikeJson = mockMvc.perform(get("/api/v1/posts/{slug}/comments", postSlug))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<CommentDTO> commentsAfterLike = objectMapper.readValue(commentsAfterLikeJson,
                objectMapper.getTypeFactory().constructCollectionType(List.class, CommentDTO.class));
        assertThat(commentsAfterLike.get(0).likes()).isGreaterThan(0);

        // ========== STEP 10: Create Nested Comment (Reply) ==========
        // Given - Reply comment request
        String replyContent = FAKER.lorem().paragraph();
        CreateCommentRequest createReplyRequest = new CreateCommentRequest(replyContent, createdComment.id().toString());

        // When - Create reply
        String replyResponseJson = mockMvc.perform(post("/api/v1/posts/{slug}/comments", postSlug)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReplyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(replyContent))
                .andReturn()
                .getResponse()
                .getContentAsString();

        CommentDTO createdReply = objectMapper.readValue(replyResponseJson, CommentDTO.class);

        // Then - Verify reply was created
        assertThat(createdReply.content()).isEqualTo(replyContent);
        assertThat(createdReply.parentId()).isEqualTo(createdComment.id());

        // Verify comments count was incremented again
        String postAfterReplyJson = mockMvc.perform(get("/api/v1/posts/{slug}", postSlug))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentsCount").value(2))
                .andReturn()
                .getResponse()
                .getContentAsString();

        PostDTO postAfterReply = objectMapper.readValue(postAfterReplyJson, PostDTO.class);
        assertThat(postAfterReply.commentsCount()).isEqualTo(2);

        // ========== STEP 11: Verify Final State ==========
        // When - Get final post state
        String finalPostJson = mockMvc.perform(get("/api/v1/posts/{slug}", postSlug))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PostDTO finalPost = objectMapper.readValue(finalPostJson, PostDTO.class);

        // Then - Verify all interactions are reflected
        assertThat(finalPost.views()).isEqualTo(1L);
        assertThat(finalPost.likes()).isEqualTo(1L);
        assertThat(finalPost.commentsCount()).isEqualTo(2);
        assertThat(finalPost.title()).isEqualTo(postTitle);
        assertThat(finalPost.slug()).isEqualTo(postSlug);
    }

    @Test
    void itShouldRejectLoginWithInvalidCredentials() throws Exception {
        // Given - Invalid login credentials
        LoginRequest invalidLoginRequest = new LoginRequest(
                FAKER.internet().emailAddress(),
                FAKER.internet().password()
        );

        // When & Then - Login should fail
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void itShouldInitializePostWithZeroViews() throws Exception {
        // Given - Create post
        Author author = createTestAuthor();
        Tag tag = createTestTag();
        String postSlug = String.join("-", FAKER.lorem().words(3)).toLowerCase();
        CreatePostRequest createPostRequest = new CreatePostRequest(
                FAKER.lorem().sentence(),
                postSlug,
                FAKER.lorem().paragraph(),
                FAKER.lorem().sentence(),
                FAKER.internet().image(),
                author.getId().toString(),
                List.of(tag.getId().toString()),
                true,
                FAKER.random().nextInt(5, 30)
        );

        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPostRequest)))
                .andExpect(status().isCreated());

        // When - Get post
        String postJson = mockMvc.perform(get("/api/v1/posts/{slug}", postSlug))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PostDTO post = objectMapper.readValue(postJson, PostDTO.class);

        // Then - Verify views start at zero
        assertThat(post.views()).isEqualTo(0L);
    }

    @Test
    void itShouldPreventLikesFromGoingBelowZero() throws Exception {
        // Given - Create post
        Author author = createTestAuthor();
        Tag tag = createTestTag();
        String postSlug = String.join("-", FAKER.lorem().words(3)).toLowerCase();
        CreatePostRequest createPostRequest = new CreatePostRequest(
                FAKER.lorem().sentence(),
                postSlug,
                FAKER.lorem().paragraph(),
                FAKER.lorem().sentence(),
                FAKER.internet().image(),
                author.getId().toString(),
                List.of(tag.getId().toString()),
                true,
                FAKER.random().nextInt(5, 30)
        );

        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPostRequest)))
                .andExpect(status().isCreated());

        // When - Like and then unlike
        mockMvc.perform(post("/api/v1/posts/{slug}/like", postSlug))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/v1/posts/{slug}/like", postSlug))
                .andExpect(status().isOk());

        // Then - Verify likes don't go below zero
        String postJson = mockMvc.perform(get("/api/v1/posts/{slug}", postSlug))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PostDTO post = objectMapper.readValue(postJson, PostDTO.class);
        assertThat(post.likes()).isEqualTo(0L);
    }

    @Test
    void itShouldAllowAdminToManageContentToFullEffect() throws Exception {
        // Ensure an author with ID 1 exists for comments (CommentController hardcodes authorId = 1L)
        try {
            jdbcTemplate.queryForObject("SELECT id FROM authors WHERE id = 1", Long.class);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            jdbcTemplate.update(
                    "INSERT INTO authors (id, name, username, email, created_at, updated_at) VALUES (1, ?, ?, ?, ?, ?)",
                    FAKER.name().fullName(),
                    FAKER.name().username(),
                    FAKER.internet().emailAddress(),
                    java.time.LocalDateTime.now(),
                    java.time.LocalDateTime.now()
            );
            jdbcTemplate.execute("SELECT setval('authors_id_seq', (SELECT MAX(id) FROM authors))");
        }

        // ========== STEP 1: Login as Admin ==========
        TestUser testAdminUser = createTestUser();
        User adminUser = testAdminUser.user;
        com.iabdinur.dto.AuthenticationRequest authRequest = new com.iabdinur.dto.AuthenticationRequest(
                adminUser.getEmail(), testAdminUser.plainPassword);

        String loginResponseJson = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        com.iabdinur.dto.AuthenticationResponse authResponse = objectMapper.readValue(loginResponseJson, com.iabdinur.dto.AuthenticationResponse.class);
        String authToken = authResponse.token();
        assertThat(authToken).isNotNull();

        // ========== STEP 2: Create Tag ==========
        String tagName = FAKER.lorem().word();
        String tagSlug = String.join("-", FAKER.lorem().words(2)).toLowerCase();
        String tagDescription = FAKER.lorem().sentence();
        CreateTagRequest createTagRequest = new CreateTagRequest(tagName, tagSlug, tagDescription);

        String tagResponseJson = mockMvc.perform(post("/api/v1/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTagRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TagDTO createdTag = objectMapper.readValue(tagResponseJson, TagDTO.class);
        assertThat(createdTag.name()).isEqualTo(tagName);
        assertThat(createdTag.slug()).isEqualTo(tagSlug);

        // ========== STEP 3: Update Tag ==========
        String updatedTagName = FAKER.lorem().word();
        String updatedTagSlug = String.join("-", FAKER.lorem().words(2)).toLowerCase();
        String updatedTagDescription = FAKER.lorem().sentence();
        CreateTagRequest updateTagRequest = new CreateTagRequest(updatedTagName, updatedTagSlug, updatedTagDescription);

        String updatedTagResponseJson = mockMvc.perform(put("/api/v1/tags/{slug}", tagSlug)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTagRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TagDTO updatedTag = objectMapper.readValue(updatedTagResponseJson, TagDTO.class);
        assertThat(updatedTag.name()).isEqualTo(updatedTagName);
        assertThat(updatedTag.slug()).isEqualTo(updatedTagSlug);
        assertThat(updatedTag.description()).isEqualTo(updatedTagDescription);

        // ========== STEP 4: Create Author ==========
        String authorName = FAKER.name().fullName();
        String authorUsername = FAKER.name().username();
        String authorEmail = FAKER.internet().emailAddress();
        String authorBio = FAKER.lorem().paragraph();
        CreateAuthorRequest createAuthorRequest = new CreateAuthorRequest(
                authorName,
                authorUsername,
                authorEmail,
                authorBio,
                FAKER.internet().avatar(),
                FAKER.internet().image(),
                FAKER.address().city(),
                FAKER.internet().url(),
                "https://twitter.com/" + authorUsername,
                "https://github.com/" + authorUsername,
                "https://linkedin.com/in/" + authorUsername
        );

        String authorResponseJson = mockMvc.perform(post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAuthorRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuthorDTO createdAuthor = objectMapper.readValue(authorResponseJson, AuthorDTO.class);
        assertThat(createdAuthor.name()).isEqualTo(authorName);
        assertThat(createdAuthor.username()).isEqualTo(authorUsername);

        // ========== STEP 5: Update Author ==========
        String updatedAuthorName = FAKER.name().fullName();
        String updatedAuthorBio = FAKER.lorem().paragraph();
        CreateAuthorRequest updateAuthorRequest = new CreateAuthorRequest(
                updatedAuthorName,
                authorUsername, // Keep same username
                authorEmail, // Keep same email
                updatedAuthorBio,
                FAKER.internet().avatar(),
                FAKER.internet().image(),
                FAKER.address().city(),
                FAKER.internet().url(),
                "https://twitter.com/" + authorUsername,
                "https://github.com/" + authorUsername,
                "https://linkedin.com/in/" + authorUsername
        );

        String updatedAuthorResponseJson = mockMvc.perform(put("/api/v1/authors/{username}", authorUsername)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAuthorRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuthorDTO updatedAuthor = objectMapper.readValue(updatedAuthorResponseJson, AuthorDTO.class);
        assertThat(updatedAuthor.name()).isEqualTo(updatedAuthorName);
        assertThat(updatedAuthor.bio()).isEqualTo(updatedAuthorBio);
        assertThat(updatedAuthor.username()).isEqualTo(authorUsername); // Username should remain the same

        // ========== STEP 6: Create Post ==========
        String postTitle = FAKER.lorem().sentence();
        String postSlug = String.join("-", FAKER.lorem().words(3)).toLowerCase();
        String postContent = FAKER.lorem().paragraph(10);
        String postExcerpt = FAKER.lorem().sentence();
        String postCoverImage = FAKER.internet().image();
        Integer readingTime = FAKER.random().nextInt(5, 30);
        CreatePostRequest createPostRequest = new CreatePostRequest(
                postTitle,
                postSlug,
                postContent,
                postExcerpt,
                postCoverImage,
                createdAuthor.id().toString(),
                List.of(updatedTag.id().toString()),
                true, // isPublished
                readingTime
        );

        String postResponseJson = mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPostRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PostDTO createdPost = objectMapper.readValue(postResponseJson, PostDTO.class);
        assertThat(createdPost.title()).isEqualTo(postTitle);
        assertThat(createdPost.slug()).isEqualTo(postSlug);

        // ========== STEP 7: Get Post Admin View ==========
        String adminPostResponseJson = mockMvc.perform(get("/api/v1/posts/{slug}/admin", postSlug))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PostDTO adminPostView = objectMapper.readValue(adminPostResponseJson, PostDTO.class);
        assertThat(adminPostView.title()).isEqualTo(postTitle);
        assertThat(adminPostView.slug()).isEqualTo(postSlug);
        assertThat(adminPostView.content()).isEqualTo(postContent);

        // ========== STEP 8: Update Post ==========
        String updatedPostTitle = FAKER.lorem().sentence();
        String updatedPostContent = FAKER.lorem().paragraph(10);
        String updatedPostExcerpt = FAKER.lorem().sentence();
        CreatePostRequest updatePostRequest = new CreatePostRequest(
                updatedPostTitle,
                postSlug, // Keep same slug
                updatedPostContent,
                updatedPostExcerpt,
                postCoverImage,
                createdAuthor.id().toString(),
                List.of(updatedTag.id().toString()),
                true,
                readingTime
        );

        String updatedPostResponseJson = mockMvc.perform(put("/api/v1/posts/{slug}", postSlug)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePostRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PostDTO updatedPost = objectMapper.readValue(updatedPostResponseJson, PostDTO.class);
        assertThat(updatedPost.title()).isEqualTo(updatedPostTitle);
        assertThat(updatedPost.content()).isEqualTo(updatedPostContent);
        assertThat(updatedPost.excerpt()).isEqualTo(updatedPostExcerpt);
        assertThat(updatedPost.slug()).isEqualTo(postSlug); // Slug should remain the same

        // ========== STEP 9: Delete Post ==========
        mockMvc.perform(delete("/api/v1/posts/{slug}", postSlug))
                .andExpect(status().isOk());

        // Verify post is deleted
        mockMvc.perform(get("/api/v1/posts/{slug}", postSlug))
                .andExpect(status().isNotFound());

        // ========== STEP 10: Delete Tag ==========
        mockMvc.perform(delete("/api/v1/tags/{slug}", updatedTagSlug))
                .andExpect(status().isOk());

        // Verify tag is deleted
        mockMvc.perform(get("/api/v1/tags/{slug}", updatedTagSlug))
                .andExpect(status().isNotFound());

        // ========== STEP 11: Delete Author ==========
        mockMvc.perform(delete("/api/v1/authors/{username}", authorUsername))
                .andExpect(status().isOk());

        // Verify author is deleted
        mockMvc.perform(get("/api/v1/authors/{idOrUsername}", authorUsername))
                .andExpect(status().isNotFound());
    }
}
