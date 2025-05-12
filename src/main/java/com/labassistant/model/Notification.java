// Notification.java Entity
package com.labassistant.model;

import com.labassistant.model.User;
import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    
    private String message;
    private Boolean isRead = false;
    private Long relatedEntityId; // Experiment/Submission ID
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    public enum NotificationType {
        NEW_COMMENT, SUBMISSION_UPDATE,NEW_QUESTION, QUESTION_ANSWERED, SYSTEM_ALERT,
    }
    // Add getters and setters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public NotificationType getType() { return type; }
    public String getMessage() { return message; }
    public Boolean getIsRead() { return isRead; }
    public Long getRelatedEntityId() { return relatedEntityId; }
    public Date getCreatedAt() { return createdAt; }

    public void setUser(User user) { this.user = user; }
    public void setType(NotificationType type) { this.type = type; }
    public void setMessage(String message) { this.message = message; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    public void setRelatedEntityId(Long relatedEntityId) { this.relatedEntityId = relatedEntityId; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

}