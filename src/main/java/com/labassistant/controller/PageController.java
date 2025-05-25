package com.labassistant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

    // Main page
    @GetMapping("/")
    public String mainPage(Model model) {
        // Redirect to the physics main page
        return "redirect:/physics";
    }

    // Login page
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Registration page
    @GetMapping("/registration")
    public String registrationPage() {
        return "register";
    }

    // Experiments main page
    @GetMapping({"/experiments", "/experiments/"})
    public String experimentsMainPage(Model model) {
        return "experiments/index";
    }

    // Experiments subpages, e.g. /experiments/floating-ping-pong-ball, etc.
    @GetMapping({"/experiments/{experiment}", "/experiments/{experiment}/"})
    public String experimentSubPage(@PathVariable String experiment, Model model) {
        // This will render templates/experiments/{experiment}/index.html if it exists
        return "experiments/" + experiment + "/index";
    }

    // Physics main page
    @GetMapping("/physics")
    public String physicsMainPage(Model model) {
        // Check if user is authenticated
        org.springframework.security.core.Authentication authentication =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"));

        if (isAuthenticated) {
            // Add user object to model for Thymeleaf
            Object principal = authentication.getPrincipal();
            // If you have a custom UserDetails, adjust the casting below
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                org.springframework.security.core.userdetails.UserDetails userDetails =
                        (org.springframework.security.core.userdetails.UserDetails) principal;
                model.addAttribute("user", userDetails);
            } else {
                // fallback: add principal as user
                model.addAttribute("user", principal);
            }
            return "physics/index-logged";
        } else {
            return "physics/index";
        }
    }

    // Physics subpages, e.g. /physics/air-cannon, /physics/cartesian-diver, etc.
    @GetMapping("/physics/{page}")
    public String physicsSubPage(@PathVariable String page, Model model) {
        // Add any dynamic attributes to the model if needed
        // This will render templates/physics/{page}.html if it exists
        return "physics/" + page;
    }
}
