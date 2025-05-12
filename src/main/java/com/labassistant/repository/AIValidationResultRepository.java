package com.labassistant.repository;

import com.labassistant.model.ai.AIValidationResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AIValidationResultRepository extends JpaRepository<AIValidationResult, Long> {
}