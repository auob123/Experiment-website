package com.labassistant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.context.annotation.Profile;
@Profile("!test")
@RestController
@RequestMapping("/api")
public class TestEndpointController {

    @GetMapping("/public/test")
    public ResponseEntity<String> publicTest() {
        return ResponseEntity.ok("Public endpoint accessible");
    }

    @GetMapping("/protected/test")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> protectedTest() {
        return ResponseEntity.ok("Protected endpoint accessible");
    }
}
