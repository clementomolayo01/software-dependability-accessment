package com.urlshortener.service;

import com.urlshortener.entity.ShortUrl;
import com.urlshortener.repository.ShortUrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

/**
 * Service for URL shortening operations with JML specifications.
 */
@Service
@Transactional
public class UrlShortenerService {

    private final ShortUrlRepository shortUrlRepository;
    private static final int SHORT_CODE_LENGTH = 8;
    private static final String BASE_URL = "http://localhost:8080/";

    @Autowired
    public UrlShortenerService(ShortUrlRepository shortUrlRepository) {
        this.shortUrlRepository = shortUrlRepository;
    }

    /**
     * Shortens a long URL to a short code.
     * 
     * @param originalUrl The original URL to shorten (must not be null or empty)
     * @param username The username creating the short URL (can be null for anonymous)
     * @return The short code for the URL
     * 
     * @requires originalUrl != null && !originalUrl.isEmpty() && isValidUrl(originalUrl)
     * @ensures \result != null && !\result.isEmpty() && \result.length() == SHORT_CODE_LENGTH
     * @ensures shortUrlRepository.findByShortCode(\result).isPresent()
     */
    public String shortenUrl(String originalUrl, String username) {
        //@ assert originalUrl != null && !originalUrl.isEmpty();
        
        if (originalUrl == null || originalUrl.isEmpty()) {
            throw new IllegalArgumentException("Original URL cannot be null or empty");
        }
        
        if (!isValidUrl(originalUrl)) {
            throw new IllegalArgumentException("Invalid URL format");
        }
        
        String shortCode = generateShortCode(originalUrl);
        
        // Ensure uniqueness
        while (shortUrlRepository.findByShortCode(shortCode).isPresent()) {
            shortCode = generateShortCode(originalUrl + System.currentTimeMillis());
        }
        
        ShortUrl shortUrl = new ShortUrl(shortCode, originalUrl, username);
        shortUrlRepository.save(shortUrl);
        
        //@ assert shortCode != null && !shortCode.isEmpty();
        //@ assert shortUrlRepository.findByShortCode(shortCode).isPresent();
        return shortCode;
    }

    /**
     * Retrieves the original URL from a short code.
     * 
     * @param shortCode The short code to look up (must not be null or empty)
     * @return Optional containing the original URL if found, empty otherwise
     * 
     * @requires shortCode != null && !shortCode.isEmpty()
     * @ensures \result != null
     * @ensures shortUrlRepository.findByShortCode(shortCode).isPresent() ==>
     *          \result.isPresent() && \result.get().equals(shortUrlRepository.findByShortCode(shortCode).get().getOriginalUrl())
     */
    public Optional<String> getOriginalUrl(String shortCode) {
        //@ assert shortCode != null && !shortCode.isEmpty();
        
        if (shortCode == null || shortCode.isEmpty()) {
            return Optional.empty();
        }
        
        Optional<ShortUrl> shortUrl = shortUrlRepository.findByShortCode(shortCode);
        
        if (shortUrl.isEmpty()) {
            return Optional.empty();
        }
        
        ShortUrl url = shortUrl.get();
        
        if (url.isExpired()) {
            return Optional.empty();
        }
        
        // Increment click count
        url.incrementClickCount();
        shortUrlRepository.save(url);
        
        //@ assert \result != null;
        return Optional.of(url.getOriginalUrl());
    }

    /**
     * Gets statistics for a short URL.
     * 
     * @param shortCode The short code to get statistics for
     * @return Optional containing the ShortUrl entity with statistics
     * 
     * @requires shortCode != null && !shortCode.isEmpty()
     * @ensures \result != null
     */
    public Optional<ShortUrl> getStatistics(String shortCode) {
        //@ assert shortCode != null && !shortCode.isEmpty();
        
        if (shortCode == null || shortCode.isEmpty()) {
            return Optional.empty();
        }
        
        //@ assert \result != null;
        return shortUrlRepository.findByShortCode(shortCode);
    }

    /**
     * Generates a short code from a URL using SHA-256 hashing.
     * 
     * @param url The URL to generate a code for
     * @return A short code of length SHORT_CODE_LENGTH
     * 
     * @requires url != null && !url.isEmpty()
     * @ensures \result != null && \result.length() == SHORT_CODE_LENGTH
     */
    private String generateShortCode(String url) {
        //@ assert url != null && !url.isEmpty();
        
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(url.getBytes(StandardCharsets.UTF_8));
            String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
            
            // Take first SHORT_CODE_LENGTH characters and ensure alphanumeric
            String code = encoded.substring(0, Math.min(SHORT_CODE_LENGTH, encoded.length()))
                    .replaceAll("[^a-zA-Z0-9]", "A");
            
            // Pad if necessary
            while (code.length() < SHORT_CODE_LENGTH) {
                code += "A";
            }
            
            //@ assert code != null && code.length() == SHORT_CODE_LENGTH;
            return code.substring(0, SHORT_CODE_LENGTH);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Validates if a string is a valid URL.
     * 
     * @param url The URL string to validate
     * @return true if the URL is valid, false otherwise
     * 
     * @requires url != null
     * @ensures \result == (url.startsWith("http://") || url.startsWith("https://"))
     */
    private boolean isValidUrl(String url) {
        //@ assert url != null;
        boolean result = url.startsWith("http://") || url.startsWith("https://");
        //@ assert \result == (url.startsWith("http://") || url.startsWith("https://"));
        return result;
    }
}

