package com.labassistant.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    
    @ManyToOne
    @JoinColumn(name = "experiment_id", nullable = false)
    private Experiment experiment;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, length = 1000)
    private String content;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CommentStatus status = CommentStatus.PENDING;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt = new Date();

    public enum CommentStatus {
        PENDING, APPROVED, REJECTED
    }

    // Getters
    public Long getCommentId() { return commentId; }
    public Experiment getExperiment() { return experiment; }
    public User getUser() { return user; }
    public String getContent() { return content; }
    public CommentStatus getStatus() { return status; }
    public Date getCreatedAt() { return createdAt; }

    // Setters
    public void setCommentId(Long commentId) { 
        this.commentId = commentId; 
    }
    public void setExperiment(Experiment experiment) { this.experiment = experiment; }
    public void setUser(User user) { this.user = user; }
    public void setContent(String content) { this.content = content; }
    public void setStatus(CommentStatus status) { this.status = status; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
