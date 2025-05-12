package com.labassistant.service;

import com.labassistant.model.Step;
import com.labassistant.model.ai.AIValidationResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@Service
public class MistralAIServiceImpl implements AIService {

    private final RestTemplate restTemplate;

    @Value("${mistral.ai.api.url}")
    private String apiUrl;

    @Value("${mistral.ai.api.key:}")
    private String apiKey;

    private static final String MODEL = "mistral-tiny"; // or another available model

    public MistralAIServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public AIValidationResult validateInstruction(String instruction) {
        String prompt = "Validate this instruction:\n" + instruction + "\nОтветь на русском языке.";
        String aiReply = callMistralAPI(prompt);
        return new AIValidationResult(
            aiReply != null && aiReply.toLowerCase().contains("valid"),
            aiReply != null ? aiReply : "No response from AI",
            "ref-instruction"
        );
    }

    @Override
    public AIValidationResult validateExperimentSteps(List<Step> steps) {
        StringBuilder sb = new StringBuilder();
        sb.append("Validate these experiment steps for completeness and scientific accuracy. List missing items and recommendations. Reply in max 3 lines:\n");
        for (Step step : steps) {
            sb.append("- ").append(step.getDescription()).append("\n");
        }
        String prompt = sb.toString() + "\nОтветь на русском языке.";
        String aiReply = callMistralAPI(prompt);
        boolean isValid = true;
        if (aiReply != null) {
            String replyLower = aiReply.toLowerCase();
            boolean hasNegative = replyLower.contains("missing") || replyLower.contains("incomplete") || replyLower.contains("invalid");
            boolean hasPositive = replyLower.contains("generally complete") || replyLower.contains("scientifically accurate") || replyLower.contains("steps provided are complete");
            // If feedback is generally positive, treat as valid even if minor items are missing
            if (hasNegative && !hasPositive) {
                isValid = false;
            }
        } else {
            isValid = false;
        }
        return new AIValidationResult(
            isValid,
            aiReply != null ? aiReply : "No response from AI",
            String.valueOf(steps.size())
        );
    }

    @Override
    public String generateFeedbackRecommendation(String submissionText) {
        String prompt = "Generate feedback for this submission:\n" + submissionText;
        String aiReply = callMistralAPI(prompt);
        return aiReply != null ? aiReply : "No response from AI";
    }

    private String callMistralAPI(String prompt) {
        try {
            // Build request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", MODEL);
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "user", "content", prompt));
            requestBody.put("messages", messages);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (apiKey != null && !apiKey.isEmpty()) {
                headers.set("Authorization", "Bearer " + apiKey);
            }

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Parse the response to extract the AI's reply
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                JsonNode choices = root.path("choices");
                if (choices.isArray() && choices.size() > 0) {
                    JsonNode message = choices.get(0).path("message");
                    if (message.has("content")) {
                        return message.get("content").asText();
                    }
                }
                // Fallback: return the whole response if parsing fails
                return response.getBody();
            } else {
                return "AI API error: " + response.getStatusCode();
            }
        } catch (HttpClientErrorException e) {
            return "AI API error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString();
        } catch (Exception e) {
            return "AI API exception: " + e.getMessage();
        }
    }
}
