package com.labassistant.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "materials")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "material_name", unique = true, nullable = false)
    private String name;
 @OneToMany(mappedBy = "material", cascade = CascadeType.ALL)
    private List<ExperimentMaterial> experimentMaterials = new ArrayList<>();

    // Getter
    public List<ExperimentMaterial> getExperimentMaterials() {
        return experimentMaterials;
    }
    private String materialName;

    // Add getter
    public String getMaterialName() {
        return materialName;}
    // Getters and Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}