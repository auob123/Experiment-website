// F:/spring_app/src/main/java/com/labassistant/payload/request/SubmissionRequest.java
package com.labassistant.payload.request;

import org.springframework.web.multipart.MultipartFile;

public class SubmissionRequest {
    private Long experimentId;
    private String observations;
    private MultipartFile[] files;

    // Getters and setters
    public Long getExperimentId() { return experimentId; }
    public String getObservations() { return observations; }
    public MultipartFile[] getFiles() { return files; }

    public void setExperimentId(Long experimentId) { this.experimentId = experimentId; }
    public void setObservations(String observations) { this.observations = observations; }
    public void setFiles(MultipartFile[] files) { this.files = files; }
}