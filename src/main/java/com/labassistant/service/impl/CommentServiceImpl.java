package com.labassistant.service.impl;

import com.labassistant.model.Comment;
import com.labassistant.model.Comment.CommentStatus;
import com.labassistant.repository.CommentRepository;
import com.labassistant.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Optional<Comment> getCommentById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    @Override
    public List<Comment> getCommentsByExperiment(Long experimentId) {
        return commentRepository.findByExperiment_Id(experimentId);
    }

    @Override
    public List<Comment> getPendingComments() {
        return commentRepository.findByStatus(CommentStatus.PENDING);
    }

    @Override
    public Comment updateStatus(Long commentId, CommentStatus status) {
        return commentRepository.findById(commentId)
                .map(comment -> {
                    comment.setStatus(status);
                    return commentRepository.save(comment);
                })
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
    }

    @Override
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
