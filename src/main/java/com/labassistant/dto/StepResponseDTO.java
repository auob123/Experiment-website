// StepResponseDTO.java
package com.labassistant.dto;

public record StepResponseDTO(
    int stepNumber,
    String description,
    String requiredMaterials,
    Integer expectedDuration,
    String imageUrl
) {}