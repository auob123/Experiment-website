package com.labassistant.payload.response;

public class SubmissionResponse {
    private Long submissionId;
    private String status;

    // Конструктор
    public SubmissionResponse(Long submissionId, String status) {
        this.submissionId = submissionId;
        this.status = status;
    }

    // Геттеры
    public Long getSubmissionId() {
        return submissionId;
    }

    public String getStatus() {
        return status;
    }
}