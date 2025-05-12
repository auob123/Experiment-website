package com.labassistant.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.labassistant.security.TestSecurityController;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Use the new profile
@Import(com.labassistant.controller.TestEndpointController.class)
public class SecurityConfigTest {

    // @RestController
    // @Profile("test")
    // static class TestController {
    //     @GetMapping("/api/public/test")
    //     public String publicEndpoint() {
    //         return "public";
    //     }

    //     @GetMapping("/api/protected/test") 
    //     public String protectedEndpoint() {
    //         return "protected";
    //     }
    // }

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfigTest.class);
    
    @Autowired
    private MockMvc mockMvc;

    @Test
    void protectedEndpoint_WithoutAuth_ShouldBeUnauthorized() throws Exception {
        logger.debug("Testing unauthorized access to protected endpoint");
        try {
            mockMvc.perform(get("/api/protected/test"))
                    .andExpect(status().isUnauthorized());
        } catch (Exception e) {
            logger.error("Unauthorized access test failed: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void protectedEndpoint_WithAuth_ShouldSucceed() throws Exception {
        logger.debug("Testing authorized access to protected endpoint");
        try {
            mockMvc.perform(get("/api/protected/test"))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            logger.error("Authorized access test failed: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Test
    void publicEndpoint_ShouldBeAccessible() throws Exception {
        logger.debug("Testing public endpoint accessibility");
        try {
            mockMvc.perform(get("/api/public/test"))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            logger.error("Public endpoint test failed: {}", e.getMessage(), e);
            throw e;
        }
    }
}
