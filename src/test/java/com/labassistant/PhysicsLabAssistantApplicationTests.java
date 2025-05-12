package com.labassistant;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;

import com.labassistant.repository.CategoryRepository;
import com.labassistant.repository.RoleRepository;
import com.labassistant.repository.UserRepository;
import com.labassistant.repository.AIInteractionRepository;
import com.labassistant.repository.AIValidationResultRepository;
import com.labassistant.security.jwt.JwtAuthFilter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@SpringBootTest
@AutoConfigureMockMvc // Add this annotation to enable MockMvc
@ActiveProfiles("test")
class PhysicsLabAssistantApplicationTests {

    private static final Logger logger = LoggerFactory.getLogger(PhysicsLabAssistantApplicationTests.class);
    
    @MockBean
    private JwtAuthFilter jwtAuthFilter;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private RoleRepository roleRepository;
    @MockBean
    private CategoryRepository categoryRepository;
    @MockBean
    private AIInteractionRepository aiInteractionRepository;
    @MockBean
    private AIValidationResultRepository aiValidationResultRepository;

    @Test
    void contextLoads() {
        logger.debug("Spring context loaded successfully");
    }
}