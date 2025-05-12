package com.labassistant.model;

import jakarta.persistence.*;
import java.util.*;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
@Table(name = "ExperimentMaterials")
public class ExperimentMaterial {
    
    @EmbeddedId
    private ExperimentMaterialId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("experimentId")
    @JoinColumn(name = "experiment_id")
    private Experiment experiment;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("materialId")
    @JoinColumn(name = "material_id")
    private Material material;

    private String quantity;
    private String notes;

    // Constructors, getters and setters
    public ExperimentMaterial() {}

    // Getters and setters
    public ExperimentMaterialId getId() { return id; }
    public void setId(ExperimentMaterialId id) { this.id = id; }
    public Experiment getExperiment() { return experiment; }
    public void setExperiment(Experiment experiment) { this.experiment = experiment; }
    public Material getMaterial() { return material; }
    public void setMaterial(Material material) { this.material = material; }
    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}