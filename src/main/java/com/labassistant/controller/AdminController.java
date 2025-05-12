// AdminController.java
package com.labassistant.controller;

import com.labassistant.model.User;
import com.labassistant.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        logger.debug("Admin accessing all users");
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PutMapping("/users/{userId}/roles")
    public ResponseEntity<?> updateUserRoles(
            @PathVariable Long userId,
            @RequestBody Map<String, List<String>> roleUpdates) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            logger.info("Updating roles for user: {}", userId);
            // Add role update logic here
            return ResponseEntity.ok(Map.of("message", "Roles updated successfully"));
        } catch (Exception e) {
            logger.error("Role update error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/content/{contentId}")
    public ResponseEntity<?> moderateContent(@PathVariable Long contentId) {
        logger.debug("Moderating content ID: {}", contentId);
        // Add content moderation logic
        return ResponseEntity.ok(Map.of("message", "Content moderated"));
    }

    @PatchMapping("/content/{contentId}/approve")
    public ResponseEntity<?> approveContent(@PathVariable Long contentId) {
        logger.debug("Approving content ID: {}", contentId);
        // Add logic to approve content
        return ResponseEntity.ok(Map.of("message", "Content approved successfully"));
    }

    @PatchMapping("/content/{contentId}/reject")
    public ResponseEntity<?> rejectContent(@PathVariable Long contentId) {
        logger.debug("Rejecting content ID: {}", contentId);
        boolean contentExists = false; // Replace with actual logic to check if content exists
        if (!contentExists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Content not found"));
        }
        // Add logic to reject content
        return ResponseEntity.ok(Map.of("message", "Content rejected successfully"));
    }

    @GetMapping("/content/{contentId}/validate")
    public ResponseEntity<?> validateContentRules(@PathVariable Long contentId) {
        logger.debug("Validating content rules for ID: {}", contentId);
        // Add logic to validate content rules
        return ResponseEntity.ok(Map.of("message", "Content validation passed"));
    }
}