package com.labassistant.model;

import com.labassistant.model.ai.AIValidationResult;
import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Instruction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    
    @Lob
    private String content;
    
    @ManyToOne
    private User author;
    
    @OneToOne(cascade = CascadeType.ALL)
    private AIValidationResult validationResult;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date publishDate;
    
    @Enumerated(EnumType.STRING)
    private InstructionStatus status;

    public enum InstructionStatus {
        DRAFT, VALIDATION_PENDING, PUBLISHED, ARCHIVED
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    public AIValidationResult getValidationResult() { return validationResult; }
    public void setValidationResult(AIValidationResult validationResult) { this.validationResult = validationResult; }
    public Date getCreationDate() { return creationDate; }
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }
    public Date getPublishDate() { return publishDate; }
    public void setPublishDate(Date publishDate) { this.publishDate = publishDate; }
    public InstructionStatus getStatus() { return status; }
    public void setStatus(InstructionStatus status) { this.status = status; }
}
