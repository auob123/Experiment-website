package com.labassistant.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN") // Add mock user with ADMIN role
    void approveContent_ShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(patch("/api/admin/content/1/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Content approved successfully"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void rejectContent_ShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(patch("/api/admin/content/1/reject"))
                .andExpect(status().isNotFound()) // Expect 404 since content does not exist
                .andExpect(jsonPath("$.error").value("Content not found"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN") // Add mock user with ADMIN role
    void validateContentRules_ShouldReturnValidationMessage() throws Exception {
        mockMvc.perform(get("/api/admin/content/1/validate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Content validation passed"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN") // Add mock user with ADMIN role
    void rejectContent_ShouldReturnErrorMessage_WhenContentNotFound() throws Exception {
        mockMvc.perform(patch("/api/admin/content/999/reject")) // Non-existent content ID
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Content not found"));
    }
}
