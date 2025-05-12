package com.labassistant.controller;

import com.labassistant.model.Question;
import com.labassistant.service.QnAService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/qna")
public class QnAController {

    private final QnAService qnaService;

    public QnAController(QnAService qnaService) {
        this.qnaService = qnaService;
    }

    @PostMapping("/questions")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Question> submitQuestion(@RequestBody Question question) {
        return ResponseEntity.ok(qnaService.submitQuestion(question));
    }

    @GetMapping("/questions/pending")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<Question>> getPendingQuestions() {
        return ResponseEntity.ok(qnaService.getPendingQuestions());
    }

    @PostMapping("/questions/{id}/answer")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Question> answerQuestion(
            @PathVariable Long id,
            @RequestBody String answer) {
        return ResponseEntity.ok(qnaService.answerQuestion(id, answer));
    }

    @GetMapping("/experiments/{expId}/questions")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    public ResponseEntity<List<Question>> getQuestionsByExperiment(
            @PathVariable Long expId) {
        return ResponseEntity.ok(qnaService.getQuestionsByExperiment(expId));
    }
}
