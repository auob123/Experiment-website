package com.labassistant.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.labassistant.model.User;
import com.labassistant.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createUser_ShouldReturnCreatedUser() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "username": "newuser",
                        "email": "newuser@example.com",
                        "password": "password123"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andDo(result -> {
                    System.out.println("✅ Пользователь успешно создан:");
                    System.out.println("   ├─ Имя пользователя: newuser");
                    System.out.println("   ├─ Электронная почта: newuser@example.com");
                });
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "username": "updateduser",
                        "email": "updateduser@example.com"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updateduser"))
                .andDo(result -> {
                    System.out.println("✅ Пользователь успешно обновлен:");
                    System.out.println("   ├─ Имя пользователя: updateduser");
                    System.out.println("   ├─ Электронная почта: updateduser@example.com");
                });
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andDo(result -> {
                    System.out.println("✅ Пользователь успешно удален:");
                    System.out.println("   └─ ID пользователя: 1");
                });
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void assignRoles_ShouldReturnOk() throws Exception {
        mockMvc.perform(put("/api/users/1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    ["ROLE_ADMIN", "ROLE_TEACHER"]
                """))
                .andExpect(status().isOk())
                .andDo(result -> {
                    System.out.println("✅ Роли успешно назначены:");
                    System.out.println("   ├─ ID пользователя: 1");
                    System.out.println("   └─ Роли: ROLE_ADMIN, ROLE_TEACHER");
                });
    }
}
