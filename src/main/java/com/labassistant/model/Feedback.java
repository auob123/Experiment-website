// Feedback.java Entity
package com.labassistant.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Feedback {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "submission_id")
    private Submission submission;
    
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;
    
    private Integer rating;
    private String comment;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    // Getters and setters
    public Long getId() { return id; }
    public Submission getSubmission() { return submission; }
    public User getTeacher() { return teacher; }
    public Integer getRating() { return rating; }
    public String getComment() { return comment; }
    public Date getCreatedAt() { return createdAt; }

    public void setSubmission(Submission submission) { this.submission = submission; }
    public void setTeacher(User teacher) { this.teacher = teacher; }
    public void setRating(Integer rating) { this.rating = rating; }
    public void setComment(String comment) { this.comment = comment; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}