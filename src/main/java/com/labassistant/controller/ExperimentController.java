package com.labassistant.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.labassistant.dto.ExperimentResponseDTO;
import com.labassistant.dto.ExperimentSearchResponse;
import com.labassistant.model.Experiment;
import com.labassistant.model.User;
import com.labassistant.model.ai.AIValidationResult;
import com.labassistant.payload.request.ExperimentRequest;
import com.labassistant.repository.UserRepository;
import com.labassistant.service.AIService;
import com.labassistant.service.ExperimentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/experiments")
public class ExperimentController {

    private final ExperimentService experimentService;
    private final UserRepository userRepository;
    private final AIService aiService;

    public ExperimentController(ExperimentService experimentService, UserRepository userRepository, AIService aiService) {
        this.experimentService = experimentService;
        this.userRepository = userRepository;
        this.aiService = aiService;
    }

@PostMapping("/search")
public ResponseEntity<List<ExperimentSearchResponse>> searchExperiments(@RequestBody String query) {
    List<ExperimentSearchResponse> results = experimentService.searchExperiments(query);
    return ResponseEntity.ok(results);
}
@PostMapping
    public ResponseEntity<?> createExperiment(@Valid @RequestBody ExperimentRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Map StepRequest to Step before AI validation
        List<com.labassistant.model.Step> stepEntities = new java.util.ArrayList<>();
        if (request.getSteps() != null && !request.getSteps().isEmpty()) {
            for (var stepReq : request.getSteps()) {
                com.labassistant.model.Step step = new com.labassistant.model.Step();
                step.setStepNumber(stepReq.getStepNumber());
                step.setDescription(stepReq.getDescription());
                step.setRequiredMaterials(stepReq.getRequiredMaterials());
                step.setExpectedDuration(stepReq.getExpectedDuration());
                step.setImageUrl(stepReq.getImageUrl());
                stepEntities.add(step);
            }
        }
        // Perform AI validation
        AIValidationResult validationResult = aiService.validateExperimentSteps(stepEntities);
        if (!validationResult.isValid()) {
            return ResponseEntity.badRequest().body(
                Collections.singletonMap("aiValidation", validationResult.getFeedback())
            );
        }

        try {
            Experiment experiment = experimentService.createExperiment(request, user);
            ExperimentResponseDTO dto = experimentService.getExperimentBySlug(experiment.getSlug());
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                Collections.singletonMap("aiValidation", e.getMessage())
            );
        }
    }
    @GetMapping
public ResponseEntity<List<ExperimentResponseDTO>> getAllExperiments() {
    return ResponseEntity.ok(experimentService.getAllExperiments());
}

@GetMapping("/{slug}")
public ResponseEntity<ExperimentResponseDTO> getExperimentBySlug(@PathVariable String slug) {
    return ResponseEntity.ok(experimentService.getExperimentBySlug(slug));
}
@PostMapping("/{id}/notify")
public ResponseEntity<?> notifyStudents(@PathVariable Long id) {
    experimentService.notifyStudents(id); // Fix: Ensure this method exists in ExperimentService
    return ResponseEntity.ok(Map.of("message", "Students notified successfully"));
}

@PostMapping("/{id}/allow-access")
@PreAuthorize("hasRole('PARENT')")
public ResponseEntity<?> allowAccess(@PathVariable Long id, @RequestParam Long studentId) {
    experimentService.allowAccess(id, studentId); // Fix: Ensure this method exists in ExperimentService
    return ResponseEntity.ok(Map.of("message", "Access granted to student"));
}
    // Existing methods...
}
