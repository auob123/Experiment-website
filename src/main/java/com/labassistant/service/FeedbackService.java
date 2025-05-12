package com.labassistant.service;

import com.labassistant.model.Feedback;
import com.labassistant.model.Submission;
import com.labassistant.model.User;
import com.labassistant.payload.request.FeedbackRequest;
import com.labassistant.repository.FeedbackRepository;
import com.labassistant.repository.SubmissionRepository;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final SubmissionRepository submissionRepository;

    public FeedbackService(FeedbackRepository feedbackRepository,
                          SubmissionRepository submissionRepository) {
        this.feedbackRepository = feedbackRepository;
        this.submissionRepository = submissionRepository;
    }

    public Feedback createFeedback(FeedbackRequest request, User teacher) {
        Submission submission = submissionRepository.findById(request.submissionId())
                .orElseThrow(() -> new RuntimeException("Submission not found"));
        
        Feedback feedback = new Feedback();
        feedback.setSubmission(submission);
        feedback.setTeacher(teacher);
        feedback.setRating(request.rating());
        feedback.setComment(request.comment());
        
        return feedbackRepository.save(feedback);
    }
}