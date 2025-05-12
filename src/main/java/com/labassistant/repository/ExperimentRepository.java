package com.labassistant.repository;

import com.labassistant.model.Experiment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ExperimentRepository extends JpaRepository<Experiment, Long> {
    Optional<Experiment> findBySlug(String slug);
    boolean existsBySlug(String slug);

    // Changed to use correct field names: title and shortDescription
    @Query("SELECT e FROM Experiment e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(e.shortDescription) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Experiment> searchByNameOrDescription(String query);
}