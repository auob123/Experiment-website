package com.labassistant.payload.request;

import jakarta.validation.constraints.*;

public record FeedbackRequest(
    @NotNull
    Long submissionId,
    
    @Min(1) @Max(5)
    Integer rating,
    
    @NotBlank
    String comment
) {}