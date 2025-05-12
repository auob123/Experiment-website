package com.labassistant;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.labassistant.model.Role;
import com.labassistant.model.User;
import com.labassistant.payload.request.LoginRequest;
import com.labassistant.repository.RoleRepository;
import com.labassistant.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
class AuthControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inject PasswordEncoder bean

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        initializeRoles();

        // Create a test user
        User user = new User();
        user.setUsername("physics_student");
        user.setPassword(passwordEncoder.encode("SecurePass123!")); // Use injected PasswordEncoder
        user.setEmail("student@lab.physics");
        user.setFirstName("Иван");
        user.setLastName("Петров");
        user.setRoles(Collections.singleton(roleRepository.findByName("ROLE_STUDENT").orElseThrow()));
        userRepository.saveAndFlush(user);

        System.out.println("✅ Test user created: " + user);
    }

    private void initializeRoles() {
        createRoleIfMissing("ROLE_STUDENT");
        createRoleIfMissing("ROLE_TEACHER");
        createRoleIfMissing("ROLE_ADMIN");
    }

    private void createRoleIfMissing(String roleName) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.saveAndFlush(role);
        }
    }

    @Test
    void testFullAuthFlow() throws Exception {
        testUserRegistration();
        testDuplicateUsername();
        testLoginAndProtectedEndpoint();
    }

    private void testUserRegistration() throws Exception {
        System.out.println("\n=== ТЕСТ РЕГИСТРАЦИИ ===");
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "username": "new_student",
                        "email": "new_student@lab.physics",
                        "password": "NewPass123!",
                        "firstName": "Тест",
                        "lastName": "Пользователь"
                    }
                """))
                .andDo(result -> {
                    User user = userRepository.findByUsername("new_student").orElseThrow();
                    System.out.println("✅ Пользователь зарегистрирован: " + user);
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        System.out.println("=== ТЕСТ ПРОЙДЕН ===\n");
    }

    private void testDuplicateUsername() throws Exception {
        System.out.println("=== ТЕСТ ДУБЛИКАТА ЛОГИНА ===");
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "username": "physics_student",
                        "email": "duplicate@mail.com",
                        "password": "DuplicatePass123!",
                        "firstName": "Дубликат",
                        "lastName": "Пользователь"
                    }
                """))
                .andDo(result -> {
                    System.out.println("❌ Ошибка: Логин 'physics_student' уже занят");
                    System.out.println("   └─ Статус: " + result.getResponse().getStatus() + " " + HttpStatus.BAD_REQUEST.getReasonPhrase());
                    System.out.println("   └─ Ответ сервера: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Username is already taken!"));

        System.out.println("=== ТЕСТ ПРОЙДЕН ===\n");
    }

    private void testLoginAndProtectedEndpoint() throws Exception {
        System.out.println("\n=== ТЕСТ АВТОРИЗАЦИИ ===");
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("physics_student");
        loginRequest.setPassword("SecurePass123!");

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
            "/api/auth/signin",
            loginRequest,
            String.class
        );

        System.out.println("Login Response: " + loginResponse.getBody());
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode(), "Login failed");

        String jwt = extractJwtToken(loginResponse.getBody());
        System.out.println("Extracted JWT: " + jwt);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);

        ResponseEntity<String> protectedResponse = restTemplate.exchange(
            "/api/test/student",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            String.class
        );

        System.out.println("Protected Endpoint Response: " + protectedResponse.getBody());
        assertEquals(HttpStatus.OK, protectedResponse.getStatusCode(), "Access to protected endpoint failed");
        assertEquals("Student Content.", protectedResponse.getBody(), "Unexpected response body");

        System.out.println("✅ Полный цикл регистрации выполнен");
        System.out.println("   ├─ Статус авторизации: " + protectedResponse.getStatusCode());
        System.out.println("   └─ Ответ эндпоинта: \"" + protectedResponse.getBody() + "\"");
    }

    private String extractJwtToken(String responseBody) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            if (jsonNode.has("token")) {
                return jsonNode.get("token").asText();
            } else if (jsonNode.has("accessToken")) {
                return jsonNode.get("accessToken").asText();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract JWT token from response: " + responseBody, e);
        }
        throw new RuntimeException("No JWT token found in response: " + responseBody);
    }
}