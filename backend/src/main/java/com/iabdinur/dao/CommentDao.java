package com.iabdinur.dao;

import com.iabdinur.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentDao {
    List<Comment> selectAllComments();
    List<Comment> selectCommentsByPostId(Long postId);
    Optional<Comment> selectCommentById(Long commentId);
    void insertComment(Comment comment);
    boolean existsCommentById(Long commentId);
    void deleteCommentById(Long commentId);
    void updateComment(Comment update);
    void incrementLikes(Long commentId);
}
