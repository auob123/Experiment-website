package com.labassistant.model.ai;

import jakarta.persistence.*;
import java.util.Date;
@Table(name = "ai_validation_results")
@Entity
public class AIValidationResult {
    private boolean valid;
    
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String feedback;
    private String aiReferenceId;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(columnDefinition = "TEXT")
    private String originalInstruction;
    
    @Column(columnDefinition = "TEXT")
    private String validatedInstruction;
    
    private String validationSource;
    private boolean safetyApproved;
    private boolean scientificAccuracy;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date validationDate;
    

    @OneToOne(mappedBy = "validationResult")

    private AIInteraction interaction;
   
    // ADDED MISSING CONSTRUCTORS
    public AIValidationResult() {}

    public AIValidationResult(boolean valid, String feedback) {
        this.valid = valid;
        this.feedback = feedback;
    }

    public AIValidationResult(boolean valid, String feedback, String aiReferenceId) {
        this.valid = valid;
        this.feedback = feedback;
        this.aiReferenceId = aiReferenceId;
    }
    public void setInteraction(AIInteraction interaction) {
        this.interaction = interaction;
        if (interaction != null) {
            interaction.setValidationResult(this);
        }}

    // GETTERS/SETTERS (UNCHANGED)
    public boolean isValid() { return valid; }
    public String getFeedback() { return feedback; }
    public String getAiReferenceId() { return aiReferenceId; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOriginalInstruction() { return originalInstruction; }
    public void setOriginalInstruction(String originalInstruction) { this.originalInstruction = originalInstruction; }
    public String getValidatedInstruction() { return validatedInstruction; }
    public void setValidatedInstruction(String validatedInstruction) { this.validatedInstruction = validatedInstruction; }
    public String getValidationSource() { return validationSource; }
    public void setValidationSource(String validationSource) { this.validationSource = validationSource; }
    public boolean isSafetyApproved() { return safetyApproved; }
    public void setSafetyApproved(boolean safetyApproved) { this.safetyApproved = safetyApproved; }
    public boolean isScientificAccuracy() { return scientificAccuracy; }
    public void setScientificAccuracy(boolean scientificAccuracy) { this.scientificAccuracy = scientificAccuracy; }
    public Date getValidationDate() { return validationDate; }
    public void setValidationDate(Date validationDate) { this.validationDate = validationDate; }
    public AIInteraction getInteraction() { return interaction; }
  
}
