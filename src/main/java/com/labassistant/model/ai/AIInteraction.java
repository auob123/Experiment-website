package com.labassistant.model.ai;

import com.labassistant.model.Experiment;
import com.labassistant.model.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Table(name = "ai_interactions")
@Entity
public class AIInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
   
    
    @ManyToOne
    private User user;
    

    @ManyToOne
private Experiment experiment;

public Experiment getExperiment() {
    return experiment;
}
    
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String outputText;
    @Column(columnDefinition = "TEXT")
    private String inputText;

    // Add missing setter
    public void setInputText(String inputText) {
        this.inputText = inputText;
    }
      // Add setter for interactionType
      public void setInteractionType(InteractionType interactionType) {
        this.interactionType = interactionType;
    }
    @Enumerated(EnumType.STRING)
    private InteractionType interactionType;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    @OneToOne(cascade = CascadeType.ALL) 
    @JoinColumn(name = "validation_result_id")
    private AIValidationResult validationResult;

    // Add getter/setter
    public AIValidationResult getValidationResult() { return validationResult; }
    public void setValidationResult(AIValidationResult validationResult) { 
        this.validationResult = validationResult;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public void setOutputText(String outputText) {
        this.outputText = outputText;
    }

    public InteractionType getInteractionType() {
        return interactionType;
    }

    // Getters and setters
    public enum InteractionType {
        VALIDATION,
        INSTRUCTION_VALIDATION,
        FEEDBACK_ANALYSIS,
        QUESTION_ANSWER
    }
}
