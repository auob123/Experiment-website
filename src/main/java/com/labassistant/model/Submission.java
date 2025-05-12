// F:/spring_app/src/main/java/com/labassistant/model/Submission.java
package com.labassistant.model;

import jakarta.persistence.*;
import com.labassistant.converter.JsonConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "submissions")
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id") // Explicitly map to submission_id column
    private Long submissionId;

    @ManyToOne
    @JoinColumn(name = "experiment_id", nullable = false)
    private Experiment experiment;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    @Convert(converter = JsonConverter.class)
    @Column(name = "submission_data", columnDefinition = "JSON")
    private Map<String, Object> submissionData;

    @Column(name = "submitted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date submittedAt = new Date();

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('SUBMITTED', 'UNDER_REVIEW', 'COMPLETED')")
    private SubmissionStatus status;

    public enum SubmissionStatus {
        SUBMITTED, UNDER_REVIEW, COMPLETED
    }

    // Getters and setters
    public Long getSubmissionId() { return submissionId; }
    public void setSubmissionId(Long submissionId) { this.submissionId = submissionId; }
    public Experiment getExperiment() { return experiment; }
    public void setExperiment(Experiment experiment) { this.experiment = experiment; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<Attachment> getAttachments() { return attachments; }
    public void setAttachments(List<Attachment> attachments) { this.attachments = attachments; }
    public Map<String, Object> getSubmissionData() { return submissionData; }
    public void setSubmissionData(Map<String, Object> submissionData) { this.submissionData = submissionData; }
    public Date getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Date submittedAt) { this.submittedAt = submittedAt; }
    public SubmissionStatus getStatus() { return status; }
    public void setStatus(SubmissionStatus status) { this.status = status; }
}