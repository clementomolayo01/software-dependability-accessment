package com.urlshortener.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        // Check if user is authenticated (token is stored in localStorage on client side)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() 
            && !authentication.getName().equals("anonymousUser")) {
            model.addAttribute("username", authentication.getName());
        }
        
        return "index";
    }
}

