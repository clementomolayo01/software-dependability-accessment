package com.urlshortener.controller;

import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.ShortenUrlResponse;
import com.urlshortener.dto.StatisticsResponse;
import com.urlshortener.entity.ShortUrl;
import com.urlshortener.service.UrlShortenerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UrlController {

    private final UrlShortenerService urlShortenerService;
    private static final String BASE_URL = "http://localhost:8080/";

    @Autowired
    public UrlController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortenUrlResponse> shortenUrl(@Valid @RequestBody ShortenUrlRequest request) {
        String username = getCurrentUsername();
        String shortCode = urlShortenerService.shortenUrl(request.getUrl(), username);
        
        ShortenUrlResponse response = new ShortenUrlResponse(
            BASE_URL + shortCode,
            shortCode,
            request.getUrl()
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats/{shortCode}")
    public ResponseEntity<StatisticsResponse> getStatistics(@PathVariable String shortCode) {
        Optional<ShortUrl> shortUrlOpt = urlShortenerService.getStatistics(shortCode);
        
        if (shortUrlOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        ShortUrl shortUrl = shortUrlOpt.get();
        StatisticsResponse response = new StatisticsResponse(
            shortUrl.getShortCode(),
            shortUrl.getOriginalUrl(),
            BASE_URL + shortUrl.getShortCode(),
            shortUrl.getClickCount(),
            shortUrl.getCreatedAt(),
            shortUrl.getExpiresAt()
        );
        
        return ResponseEntity.ok(response);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() 
            && !authentication.getName().equals("anonymousUser")) {
            return authentication.getName();
        }
        return null;
    }
}

