package com.labassistant.service;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.labassistant.dto.ExperimentResponseDTO;
import com.labassistant.dto.ExperimentSearchResponse;
import com.labassistant.dto.MaterialResponseDTO;
import com.labassistant.dto.StepResponseDTO;
import com.labassistant.exception.ResourceNotFoundException;
import com.labassistant.model.Category;
import com.labassistant.model.Experiment;
import com.labassistant.model.User;
import com.labassistant.model.ai.AIInteraction;
import com.labassistant.model.ai.AIValidationResult;
import com.labassistant.payload.request.ExperimentRequest;
import com.labassistant.repository.AIInteractionRepository;
import com.labassistant.repository.CategoryRepository;
import com.labassistant.repository.ExperimentRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class ExperimentService {
    private final AIInteractionRepository aiInteractionRepository;
    private final CategoryRepository categoryRepository; 
    private final ExperimentRepository experimentRepository;
    private final AIService aiService;

    @PersistenceContext
    private EntityManager entityManager;

    public ExperimentService(ExperimentRepository experimentRepository, 
                           AIService aiService,CategoryRepository categoryRepository,
                           AIInteractionRepository aiInteractionRepository ) {
        this.experimentRepository = experimentRepository;
        this.aiService = aiService;
        this.categoryRepository = categoryRepository;
        this.aiInteractionRepository = aiInteractionRepository;
    }

    @Transactional
    public Experiment createExperiment(ExperimentRequest request, User user) {
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        Experiment experiment = new Experiment();
        experiment.setTitle(request.getTitle());
        experiment.setCategory(category);
        experiment.setSlug(generateSlug(request.getTitle()));
        experiment.setShortDescription(request.getShortDescription());
        experiment.setCreatedBy(user);

        // Map StepRequest to Step before validation and persistence, as AI validation expects List<Step>
        List<com.labassistant.model.Step> stepEntities = new java.util.ArrayList<>();
        if (request.getSteps() != null && !request.getSteps().isEmpty()) {
            for (var stepReq : request.getSteps()) {
                com.labassistant.model.Step step = new com.labassistant.model.Step();
                step.setStepNumber(stepReq.getStepNumber());
                step.setDescription(stepReq.getDescription());
                step.setRequiredMaterials(stepReq.getRequiredMaterials());
                step.setExpectedDuration(stepReq.getExpectedDuration());
                step.setImageUrl(stepReq.getImageUrl());
                step.setExperiment(experiment);
                stepEntities.add(step);
            }
        }

        // Validate steps with AI
        AIValidationResult validationResult = aiService.validateExperimentSteps(stepEntities);
        if (validationResult == null) {
            throw new IllegalStateException("AI validation result is null");
        }
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException("AI validation failed: " + validationResult.getFeedback());
        }

        AIInteraction interaction = new AIInteraction();
        interaction.setUser(user);
        interaction.setExperiment(experiment);
        interaction.setInteractionType(AIInteraction.InteractionType.VALIDATION);
        interaction.setInputText(stepEntities.toString());
        interaction.setOutputText(validationResult.getFeedback());
        interaction.setValidationResult(validationResult);
        aiInteractionRepository.save(interaction);

        // Persist steps
        if (!stepEntities.isEmpty()) {
            experiment.setSteps(stepEntities);
        }

        Experiment savedExperiment = experimentRepository.save(experiment);
        entityManager.flush();
        return savedExperiment;
    }

    private String generateSlug(String title) {
        if (title == null) return "";
        return title.toLowerCase()
                   .replaceAll("[^a-z0-9\\s-]", "")
                   .replaceAll("\\s+", "-")
                   .replaceAll("-+", "-");
    }

    @Transactional(readOnly = true)
    public List<ExperimentSearchResponse> searchExperiments(String query) {
        return experimentRepository.searchByNameOrDescription(query)
                .stream()
                .map(experiment -> new ExperimentSearchResponse(
                        experiment.getId(),
                        experiment.getTitle(),
                        experiment.getShortDescription(),
                        experiment.getSteps()
                ))
                .collect(Collectors.toList());
    }
    public List<ExperimentResponseDTO> getAllExperiments() {
    return experimentRepository.findAll().stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
}

public ExperimentResponseDTO getExperimentBySlug(String slug) {
    return experimentRepository.findBySlug(slug)
        .map(this::convertToDTO)
        .orElseThrow(() -> new ResourceNotFoundException("Experiment not found"));
}

private ExperimentResponseDTO convertToDTO(Experiment experiment) {
    return new ExperimentResponseDTO(
        experiment.getId(),
        experiment.getSlug(),
        experiment.getTitle(),
        experiment.getShortDescription(),
        experiment.getCategory().getCategoryName(),
        experiment.getExperimentMaterials().stream()
            .map(em -> new MaterialResponseDTO(
                em.getMaterial().getMaterialName(),
                em.getQuantity(),
                em.getNotes()))
            .collect(Collectors.toList()),
        experiment.getSteps().stream()
            .map(s -> new StepResponseDTO(
                s.getStepNumber(),
                s.getDescription(),
                s.getRequiredMaterials(),
                s.getExpectedDuration(),
                s.getImageUrl()))
            .collect(Collectors.toList())
    );
}

public void notifyStudents(Long experimentId) {
    // Logic to notify students about the new experiment
    System.out.println("Notifying students about experiment ID: " + experimentId);
}

public void allowAccess(Long experimentId, Long studentId) {
    // Logic to allow access to the experiment for a specific student
    System.out.println("Allowing access to experiment ID: " + experimentId + " for student ID: " + studentId);
}
}
