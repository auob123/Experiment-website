package com.labassistant.service;

import com.labassistant.model.Step;
import com.labassistant.model.ai.AIValidationResult;
import java.util.List;

public interface AIService {
    AIValidationResult validateExperimentSteps(List<Step> steps);
    String generateFeedbackRecommendation(String submissionText);
    AIValidationResult validateInstruction(String instruction);
    
}
