package com.labassistant.controller;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.labassistant.model.Role;
import com.labassistant.model.User;
import com.labassistant.repository.RoleRepository;
import com.labassistant.repository.UserRepository;
import com.labassistant.security.WebSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Set;

@Component
public class AuthTestUtil {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    public String createUserAndGetToken(String username, String password, String roleName) throws Exception {
        // Cleanup existing data
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Create role if missing
        if (!roleRepository.existsByName(roleName)) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.saveAndFlush(role);
            System.out.println("✅ Role created: " + roleName);
        }

        // Create and save user
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(username + "@lab.physics");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRoles(Set.of(roleRepository.findByName(roleName).orElseThrow()));
        userRepository.saveAndFlush(user);
        System.out.println("✅ User created: " + user);

        // Authenticate and return token
        MvcResult result = mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                    {
                        "username": "%s",
                        "password": "%s"
                    }""", username, password)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        if (response.has("accessToken")) {
            return response.get("accessToken").asText();
        } else if (response.has("token")) {
            return response.get("token").asText();
        }
        throw new RuntimeException("No JWT token found in authentication response");
    }
}