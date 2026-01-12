package com.iabdinur.dao;

import com.iabdinur.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostDao {
    List<Post> selectAllPosts();
    List<Post> selectPublishedPosts(int limit, int offset);
    List<Post> selectPostsByAuthorId(Long authorId, int limit, int offset);
    List<Post> selectPostsByTagSlug(String tagSlug, int limit, int offset);
    Optional<Post> selectPostById(Long postId);
    Optional<Post> selectPostBySlug(String slug);
    Optional<Post> selectPublishedPostBySlug(String slug);
    void insertPost(Post post);
    boolean existsPostWithSlug(String slug);
    boolean existsPostById(Long postId);
    void deletePostById(Long postId);
    void updatePost(Post update);
    void incrementViews(Long postId);
    void incrementLikes(Long postId);
    void decrementLikes(Long postId);
    long countPublishedPosts();
    long countPostsByAuthorId(Long authorId);
    long countPostsByTagSlug(String tagSlug);
}
