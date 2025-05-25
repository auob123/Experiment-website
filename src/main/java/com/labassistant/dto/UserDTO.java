package com.labassistant.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.labassistant.model.User;
import com.labassistant.model.Role;

public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private LocalDateTime createdAt;
    private List<String> roles;

    public UserDTO(Long id, String username, String email, String firstName, String lastName, boolean isActive, LocalDateTime createdAt, List<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.roles = roles;
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.isActive = user.isActive();
        this.createdAt = user.getCreatedAt();
        Set<Role> userRoles = user.getRoles();
        this.roles = userRoles.stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }

    // Getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}
