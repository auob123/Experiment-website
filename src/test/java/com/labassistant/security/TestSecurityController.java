package com.labassistant.security;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("test") // Only active in the "test" profile
public class TestSecurityController {

    @GetMapping("/api/public/test")
    public String publicEndpoint() {
        return "public";
    }

    @GetMapping("/api/protected/test")
    public String protectedEndpoint() {
        return "protected";
    }
}