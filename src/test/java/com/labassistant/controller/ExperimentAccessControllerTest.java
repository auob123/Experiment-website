package com.labassistant.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ExperimentAccessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "teacher1", roles = "TEACHER") // Simulate a teacher user
    void notifyStudents_ShouldReturnSuccessMessage() throws Exception {
        System.out.println("\n=== ТЕСТ: Уведомление студентов ===");
        System.out.println("1. Симуляция пользователя с ролью TEACHER (имя пользователя: teacher1).");
        System.out.println("2. Отправка POST-запроса на /api/experiments/1/notify.");
        System.out.println("3. Ожидается сообщение об успешном выполнении.");

        mockMvc.perform(post("/api/experiments/1/notify"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Students notified successfully"))
                .andDo(result -> {
                    System.out.println("✅ Уведомление успешно отправлено!");
                    System.out.println("   └─ Сообщение: Students notified successfully");
                });
    }

    @Test
    @WithMockUser(username = "parent1", roles = "PARENT") // Simulate a parent user
    void allowAccess_ShouldReturnSuccessMessage() throws Exception {
        System.out.println("\n=== ТЕСТ: Разрешение доступа ===");
        System.out.println("1. Симуляция пользователя с ролью PARENT (имя пользователя: parent1).");
        System.out.println("2. Отправка POST-запроса на /api/experiments/1/allow-access с параметром studentId=2.");
        System.out.println("3. Ожидается сообщение об успешном выполнении.");

        mockMvc.perform(post("/api/experiments/1/allow-access")
                .param("studentId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Access granted to student"))
                .andDo(result -> {
                    System.out.println("✅ Доступ успешно предоставлен!");
                    System.out.println("   └─ Сообщение: Access granted to student");
                });
    }
}
