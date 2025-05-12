package com.labassistant.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.labassistant.config.JpaConfig;
import com.labassistant.model.Experiment;
import com.labassistant.model.User;
import com.labassistant.model.ai.AIValidationResult;
import com.labassistant.payload.request.ExperimentRequest;
import com.labassistant.repository.UserRepository;
import com.labassistant.security.WebSecurityConfig;
import com.labassistant.security.jwt.JwtAuthFilter;
import com.labassistant.service.AIService;
import com.labassistant.service.ExperimentService;

@SpringBootTest 
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@MockBean({
    ExperimentService.class,
    UserRepository.class,
    AIService.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Use a real/test database
public class ExperimentControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(ExperimentControllerTest.class);
    
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtAuthFilter jwtAuthFilter;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private ExperimentService experimentService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AIService aiService;

    private static final String EXPERIMENTS_ENDPOINT = "/api/experiments";
    private static final String VALID_EXPERIMENT_JSON = """
        {
            "title": "Test Experiment",
            "shortDescription": "Test Description",
            "categoryId": 1,
            "steps": []
        }""";
        
    @Test
    @WithMockUser(username = "testuser", roles = "TEACHER")
    void createExperiment_WithValidRequestAndAiValidation_ReturnsCreatedExperiment() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        
        AIValidationResult validAiResult = new AIValidationResult(true, "Validation passed");
        
        Experiment createdExperiment = new Experiment();
        createdExperiment.setId(1L);
        createdExperiment.setTitle("Test Experiment");
        createdExperiment.setShortDescription("Test Description");
        createdExperiment.setCreatedBy(mockUser);

        given(userRepository.findByUsername("testuser"))
            .willReturn(Optional.of(mockUser));
        given(aiService.validateExperimentSteps(any()))
            .willReturn(validAiResult);
        given(experimentService.createExperiment(any(ExperimentRequest.class), eq(mockUser)))
            .willReturn(createdExperiment);
        given(experimentService.getExperimentBySlug(any()))
            .willReturn(new com.labassistant.dto.ExperimentResponseDTO(
                1L, "test-experiment", "Test Experiment", "Test Description", "Some Category", java.util.List.of(), java.util.List.of()
            ));

        try {
            mockMvc.perform(post(EXPERIMENTS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(VALID_EXPERIMENT_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("Test Experiment"))
                    .andExpect(jsonPath("$.shortDescription").value("Test Description"));
        } catch (Exception e) {
            logger.error("Failed to create experiment: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void createExperiment_WithMissingTitle_ReturnsValidationError() throws Exception {
        String invalidJson = "{"
            + "\"shortDescription\": \"Test\","
            + "\"categoryId\": 1"
            + "}";
            
        try {
            mockMvc.perform(post(EXPERIMENTS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").exists());
        } catch (Exception e) {
            logger.error("Validation failed for missing title: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void createExperiment_WithMissingShortDescription_ReturnsValidationError() throws Exception {
        String invalidJson = "{"
            + "\"title\": \"Test Experiment\","
            + "\"categoryId\": 1"
            + "}";
            
        try {
            mockMvc.perform(post(EXPERIMENTS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.shortDescription").exists());
        } catch (Exception e) {
            logger.error("Validation failed for missing short description: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Test
    @WithMockUser(username = "testuser", roles = "TEACHER")
    void createExperiment_WithInvalidAiValidation_ReturnsBadRequest() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        AIValidationResult invalidAiResult = new AIValidationResult(false, "Invalid steps");
        
        given(userRepository.findByUsername("testuser"))
            .willReturn(Optional.of(mockUser));
        given(aiService.validateExperimentSteps(any()))
            .willReturn(invalidAiResult);

        try {
            mockMvc.perform(post(EXPERIMENTS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(VALID_EXPERIMENT_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.aiValidation").value("Invalid steps"));
        } catch (Exception e) {
            logger.error("AI validation failed: {}", e.getMessage(), e);
            throw e;
        }
    }
}
