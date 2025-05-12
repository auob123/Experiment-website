package com.labassistant.model;

import java.io.Serializable;
import java.util.Objects; // ADD THIS IMPORT
import jakarta.persistence.Embeddable;

@Embeddable
public class ExperimentMaterialId implements Serializable {
    private Long experimentId;
    private Long materialId;

    // Default constructor
    public ExperimentMaterialId() {}

    public ExperimentMaterialId(Long experimentId, Long materialId) {
        this.experimentId = experimentId;
        this.materialId = materialId;
    }

    // Getters and setters
    public Long getExperimentId() { return experimentId; }
    public void setExperimentId(Long experimentId) { this.experimentId = experimentId; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }

    // hashCode and equals implementation
    @Override
    public int hashCode() {
        return Objects.hash(experimentId, materialId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ExperimentMaterialId that = (ExperimentMaterialId) obj;
        return Objects.equals(experimentId, that.experimentId) &&
               Objects.equals(materialId, that.materialId);
    }
}