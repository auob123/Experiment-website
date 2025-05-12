package com.labassistant.repository;

import com.labassistant.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByStatus(Comment.CommentStatus status);
List<Comment> findByExperiment_Id(Long experimentId);
}
