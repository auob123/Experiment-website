// MaterialResponseDTO.java
package com.labassistant.dto;

public record MaterialResponseDTO(
    String materialName,
    String quantity,
    String notes
) {}