package com.labassistant.repository;

import com.labassistant.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByStatus(Question.QuestionStatus status);
    List<Question> findByExperimentId(Long expId);
}