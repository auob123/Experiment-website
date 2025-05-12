package com.labassistant.service;

import com.labassistant.model.Submission;
import com.labassistant.model.Notification.NotificationType;
import com.labassistant.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final NotificationService notificationService;

    @Autowired
    public SubmissionService(SubmissionRepository submissionRepository, 
                           NotificationService notificationService) {
        this.submissionRepository = submissionRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Submission submitExperiment(Submission submission) {
        Submission savedSubmission = submissionRepository.save(submission);
        
        notificationService.createNotification(
            submission.getUser().getId(),
            NotificationType.SUBMISSION_UPDATE,
            "New submission for experiment " + submission.getExperiment().getId(),
            savedSubmission.getSubmissionId()  // Changed from getId() to getSubmissionId()
        );
        
        return savedSubmission;
    }
}
