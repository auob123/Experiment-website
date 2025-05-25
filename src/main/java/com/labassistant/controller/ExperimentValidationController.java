package com.labassistant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.labassistant.service.AIService;
import com.labassistant.model.Step;
import com.labassistant.model.ai.AIValidationResult;
import java.util.Map;
import java.util.List;
import java.util.Collections;

@RestController
@RequestMapping("/api/experiments")
public class ExperimentValidationController {

    private final AIService aiService;

    @Autowired
    public ExperimentValidationController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateExperiment(@RequestBody Map<String, String> payload) {
        String name = payload.getOrDefault("name", "");
        String materials = payload.getOrDefault("materials", "");
        String steps = payload.getOrDefault("steps", "");

        // Create a single Step object for validation
        Step step = new Step();
        step.setStepNumber(1);
        step.setDescription(steps);
        step.setRequiredMaterials(materials);

        // Call Mistral AI via AIService
        AIValidationResult validationResult = aiService.validateExperimentSteps(Collections.singletonList(step));

        return ResponseEntity.ok(Map.of(
            "success", validationResult.isValid(),
            "feedback", validationResult.getFeedback(),
            "aiReferenceId", validationResult.getAiReferenceId(),
            "message", validationResult.getFeedback()
        ));
    }
}
