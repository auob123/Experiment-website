package com.labassistant.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.labassistant.model.Category;
import com.labassistant.model.Role;
import com.labassistant.model.User;
import com.labassistant.model.ai.AIValidationResult;
import com.labassistant.repository.CategoryRepository;
import com.labassistant.repository.RoleRepository;
import com.labassistant.repository.UserRepository;
import com.labassistant.service.AIService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class ExperimentTest {
@Autowired
private CategoryRepository categoryRepository; // Add this dependency
@Autowired
private RoleRepository roleRepository;
@Autowired
private MockMvc mockMvc;
@Autowired
private UserRepository userRepository;

@MockBean
private AIService aiService;

private String jwtToken;
private final ObjectMapper objectMapper = new ObjectMapper();
@BeforeEach
void setup() throws Exception {
    userRepository.deleteAll();
    roleRepository.deleteAll();
    categoryRepository.deleteAll();

    // Создаем все необходимые роли
    createRoleIfMissing("ROLE_USER");
    createRoleIfMissing("ROLE_STUDENT");
    createRoleIfMissing("ROLE_TEACHER");

    given(aiService.validateExperimentSteps(any()))
        .willReturn(new AIValidationResult(true, "OK"));

    // Debug: User creation
    System.out.println("\n=== НАЧАЛО СОЗДАНИЯ ПОЛЬЗОВАТЕЛЯ ===");
    MvcResult signupResult = mockMvc.perform(post("/api/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "username": "physics_user",
                    "email": "user@physics.ru",
                    "password": "Physics123!",
                    "firstName": "Дмитрий",
                    "lastName": "Иванов"
                }"""))
            .andExpect(status().isOk())
            .andReturn();
    
    System.out.println("Ответ регистрации: " + signupResult.getResponse().getContentAsString());

    User user = userRepository.findByUsernameWithRoles("physics_user")
.orElseThrow(() -> new RuntimeException("❌ User not found after signup"));
    System.out.println("✅ Пользователь создан: " + user);
    // In setup(), after user creation:
// Fetch TEACHER role and add to user
Role teacherRole = roleRepository.findByName("ROLE_TEACHER")
.orElseThrow(() -> new RuntimeException("❌ TEACHER role not found"));
Set<Role> updatedRoles = new HashSet<>(user.getRoles());
updatedRoles.add(teacherRole);
user.setRoles(updatedRoles);
userRepository.saveAndFlush(user); // Ensure roles are persistedv

    // Debug: Role assignment
    // System.out.println("\n=== ROLE ASSIGNMENT ===");
    // Set<Role> roles = new HashSet<>(user.getRoles());
    // roles.add(teacherRole);
    // user.setRoles(roles);
    // User savedUser = userRepository.saveAndFlush(user);
    // System.out.println("Updated user roles: " + savedUser.getRoles());

    // Debug: Authentication
    System.out.println("\n=== АУТЕНТИФИКАЦИЯ ===");
    MvcResult authResult = mockMvc.perform(post("/api/auth/signin")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "username": "physics_user",
                    "password": "Physics123!"
                }"""))
            .andExpect(status().isOk())
            .andReturn();

    String authResponse = authResult.getResponse().getContentAsString();
    System.out.println("Ответ входа: " + authResponse);

    JsonNode jsonNode = objectMapper.readTree(authResponse);
    System.out.println("Полный JSON ответа аутентификации:\n" + jsonNode.toPrettyString());

    if (jsonNode.has("accessToken")) {
        this.jwtToken = jsonNode.get("accessToken").asText();
    } else if (jsonNode.has("token")) {
        this.jwtToken = jsonNode.get("token").asText();
    } else {
        throw new RuntimeException("❌ No JWT token found in response");
    }
    
    System.out.println("\n=== АУТЕНТИФИКАЦИЯ УСПЕШНА ===");
    System.out.println("JWT Токен: " + jwtToken.substring(0, 20) + "...");
}
private void createRoleIfMissing(String roleName) {
    if (!roleRepository.existsByName(roleName)) {
        Role role = new Role();
        role.setName(roleName);
        roleRepository.saveAndFlush(role);}
    }

@Test
void testExperimentFlow() throws Exception {
    System.out.println("\n=== ТЕСТИРОВАНИЕ ПОТОКА ЭКСПЕРИМЕНТА ===");
    System.out.println("Используется JWT: " + jwtToken.substring(0, 20) + "...");

    Category physicsCategory = new Category();
    physicsCategory.setCategoryName("Физика");
    categoryRepository.saveAndFlush(physicsCategory);

    String experimentJson = String.format("""
    {
        "title": "Воздушная пушка",
        "shortDescription": "Демонстрация свойств атмосферного давления",
        "categoryId": %d,
        "steps": []
    }""", physicsCategory.getId());

    MvcResult createResult = mockMvc.perform(post("/api/experiments")
            .header("Authorization", "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(experimentJson))
            .andExpect(status().isCreated())
            .andReturn();

    String createResponse = createResult.getResponse().getContentAsString();
    JsonNode experimentNode = objectMapper.readTree(createResponse);
    String slug = experimentNode.get("slug").asText();

    System.out.println("\n=== ИНФОРМАЦИЯ О ЭКСПЕРИМЕНТЕ ===");
    System.out.printf("Название: '%s'%n", experimentNode.get("title").asText());
    System.out.printf("Описание: '%s'%n", experimentNode.get("shortDescription").asText());
    System.out.printf("Слаг: '%s'%n", slug);

    mockMvc.perform(get("/api/experiments")
            .header("Authorization", "Bearer " + jwtToken))
            .andExpect(status().isOk())
            .andDo(result -> {
                System.out.println("\nОтвет списка экспериментов:");
                System.out.println(objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(objectMapper.readTree(result.getResponse().getContentAsString())));
            });

    mockMvc.perform(get("/api/experiments/" + slug)
            .header("Authorization", "Bearer " + jwtToken))
            .andExpect(status().isOk())
            .andDo(result -> {
                System.out.println("\nОтвет деталей эксперимента:");
                System.out.println(objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(objectMapper.readTree(result.getResponse().getContentAsString())));
            });

    System.out.println("\n✅ ТЕСТ УСПЕШНО ЗАВЕРШЕН");
}
}