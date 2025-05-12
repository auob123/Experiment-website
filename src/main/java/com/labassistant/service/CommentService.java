package com.labassistant.service;

import com.labassistant.model.Comment;
import com.labassistant.model.Comment.CommentStatus;
import java.util.List;
import java.util.Optional;

public interface CommentService {
    Comment createComment(Comment comment);
    Optional<Comment> getCommentById(Long commentId);
    List<Comment> getCommentsByExperiment(Long experimentId);
    List<Comment> getPendingComments();
    Comment updateStatus(Long commentId, CommentStatus status);
    void deleteComment(Long commentId);
}
