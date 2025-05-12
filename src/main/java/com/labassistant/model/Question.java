package com.labassistant.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "experiment_id")
    private Experiment experiment;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Lob
    private String content;
    
    @Enumerated(EnumType.STRING)
    private QuestionStatus status;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public enum QuestionStatus {
        OPEN, ANSWERED, CLOSED, PENDING // Add PENDING
    }
    private String answer;

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Experiment getExperiment() { return experiment; }
    public void setExperiment(Experiment experiment) { this.experiment = experiment; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public QuestionStatus getStatus() { return status; }
    public void setStatus(QuestionStatus status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
