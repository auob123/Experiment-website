package com.labassistant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.labassistant.model.Submission;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
 List<Submission> findByExperiment_Id(Long experimentId); // ✅
List<Submission> findByUser_Id(Long userId); // ✅
    List<Submission> findByStatus(Submission.SubmissionStatus status);
}
