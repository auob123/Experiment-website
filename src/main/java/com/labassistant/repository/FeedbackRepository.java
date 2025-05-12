// FeedbackRepository.java
package com.labassistant.repository;

import com.labassistant.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}