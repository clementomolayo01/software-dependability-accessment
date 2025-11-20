package com.urlshortener.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // JavaScript will handle authentication check using localStorage token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        
        if (authentication != null && authentication.isAuthenticated() 
            && !authentication.getName().equals("anonymousUser")) {
            username = authentication.getName();
        }
        
        model.addAttribute("username", username);
        model.addAttribute("token", null); // Token comes from localStorage in browser
        
        return "dashboard";
    }

    @GetMapping("/stats/{shortCode}")
    public String stats(@PathVariable String shortCode, Model model) {
        // JavaScript will handle authentication check using localStorage token
        model.addAttribute("shortCode", shortCode);
        model.addAttribute("token", null); // Token comes from localStorage in browser
        
        return "stats";
    }
}

