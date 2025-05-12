// QnAService.java
package com.labassistant.service;

import com.labassistant.model.Question;
import com.labassistant.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// QnAService.java
import com.labassistant.exception.ResourceNotFoundException;
import com.labassistant.model.Notification;
import java.util.List;

@Service
public class QnAService {
    private final QuestionRepository questionRepository;
    private final NotificationService notificationService;

    public QnAService(QuestionRepository questionRepository, 
                     NotificationService notificationService) {
        this.questionRepository = questionRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Question submitQuestion(Question question) {
        Question saved = questionRepository.save(question);
        notificationService.createNotification(
            null, // Admin ID
            Notification.NotificationType.NEW_QUESTION,
            "New question submitted",
            saved.getId()
        );
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Question> getPendingQuestions() {
        return questionRepository.findByStatus(Question.QuestionStatus.PENDING);
    }

    @Transactional
    public Question answerQuestion(Long id, String answer) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        
        question.setAnswer(answer);
        question.setStatus(Question.QuestionStatus.ANSWERED);
        
        Question updated = questionRepository.save(question);
        notificationService.createNotification(
            updated.getUser().getId(),
            Notification.NotificationType.QUESTION_ANSWERED,
            "Your question has been answered",
            updated.getId()
        );
        
        return updated;
    }

    @Transactional(readOnly = true)
    public List<Question> getQuestionsByExperiment(Long expId) {
        return questionRepository.findByExperimentId(expId);
    }
}