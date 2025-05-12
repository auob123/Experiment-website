package com.labassistant.service;

import com.labassistant.model.Role;
import com.labassistant.model.User;
import com.labassistant.repository.UserRepository;
import com.labassistant.security.services.UserDetailsImpl;
import com.labassistant.security.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserDetailsServiceImplTest {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImplTest.class);
    
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        try {
            User user = new User();
            user.setUsername("testuser");
            user.setPassword("password");
            Role role = new Role();
            role.setName("ROLE_STUDENT");
            user.setRoles(Collections.singleton(role));

            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

            logger.debug("Loading user by username: testuser");
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername("testuser");
            
            assertEquals("testuser", userDetails.getUsername());
            logger.debug("User loaded successfully");
        } catch (Throwable t) {
            logger.error("User loading failed - Reason: {}", t.getMessage(), t);
            throw t;
        }
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        try {
            when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

            assertThrows(UsernameNotFoundException.class, () -> {
                userDetailsService.loadUserByUsername("unknown");
            });
        } catch (Throwable t) {
            logger.error("User not found test failed - Reason: {}", t.getMessage(), t);
            throw t;
        }
    }
}