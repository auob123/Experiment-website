package com.labassistant.payload.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import com.labassistant.payload.request.StepRequest;

public class ExperimentRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Short description is required")
    private String shortDescription;

    private Long categoryId;
    private List<StepRequest> steps;

    // Getters and setters
    public List<StepRequest> getSteps() { return steps; }
    public void setSteps(List<StepRequest> steps) { this.steps = steps; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
}
