// ExperimentResponseDTO.java
package com.labassistant.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExperimentResponseDTO(
    @JsonProperty("id") Long id,
    @JsonProperty("slug") String slug,
    @JsonProperty("title") String title,
    @JsonProperty("shortDescription") String shortDescription,
    @JsonProperty("category") String category,
    @JsonProperty("materials") List<MaterialResponseDTO> materials,
    @JsonProperty("steps") List<StepResponseDTO> steps
) {}
