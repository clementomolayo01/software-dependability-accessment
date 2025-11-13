package com.urlshortener.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> home() {
        Map<String, String> info = new HashMap<>();
        info.put("message", "URL Shortener API");
        info.put("version", "1.0.0");
        info.put("status", "Running");
        info.put("endpoints", "POST /api/shorten, GET /{shortCode}, GET /api/stats/{shortCode}");
        info.put("authentication", "POST /api/auth/register, POST /api/auth/login");
        info.put("docs", "See README.md for API documentation");
        return ResponseEntity.ok(info);
    }
}

