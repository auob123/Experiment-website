package com.labassistant.dto;

import java.util.List;

public class ExperimentSearchResponse {
    private Long id;
    private String title;
    private String shortDescription;
    private int stepCount; // Assuming steps are complex; return count instead

    public ExperimentSearchResponse(Long id, String title, String shortDescription, List<?> steps) {
        this.id = id;
        this.title = title;
        this.shortDescription = shortDescription;
        this.stepCount = steps != null ? steps.size() : 0;
    }

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getShortDescription() { return shortDescription; }
    public int getStepCount() { return stepCount; }
}