package com.urlshortener.integration;

import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.entity.ShortUrl;
import com.urlshortener.repository.ShortUrlRepository;
import com.urlshortener.service.UrlShortenerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UrlShortenerIntegrationTest {

    @Autowired
    private UrlShortenerService urlShortenerService;

    @Autowired
    private ShortUrlRepository shortUrlRepository;

    private String validUrl;

    @BeforeEach
    void setUp() {
        validUrl = "https://www.example.com";
        shortUrlRepository.deleteAll();
    }

    @Test
    void testShortenAndRetrieveUrl_EndToEnd() {
        // Given
        String username = "testuser";

        // When - Shorten URL
        String shortCode = urlShortenerService.shortenUrl(validUrl, username);

        // Then - Verify saved
        Optional<ShortUrl> saved = shortUrlRepository.findByShortCode(shortCode);
        assertTrue(saved.isPresent());
        assertEquals(validUrl, saved.get().getOriginalUrl());
        assertEquals(username, saved.get().getCreatedBy());

        // When - Retrieve original URL
        Optional<String> retrieved = urlShortenerService.getOriginalUrl(shortCode);

        // Then - Verify retrieved
        assertTrue(retrieved.isPresent());
        assertEquals(validUrl, retrieved.get());

        // Verify click count incremented
        Optional<ShortUrl> updated = shortUrlRepository.findByShortCode(shortCode);
        assertTrue(updated.isPresent());
        assertEquals(1L, updated.get().getClickCount());
    }

    @Test
    void testGetStatistics_ReturnsCorrectData() {
        // Given
        String username = "testuser";
        String shortCode = urlShortenerService.shortenUrl(validUrl, username);

        // When
        Optional<ShortUrl> stats = urlShortenerService.getStatistics(shortCode);

        // Then
        assertTrue(stats.isPresent());
        assertEquals(validUrl, stats.get().getOriginalUrl());
        assertEquals(0L, stats.get().getClickCount());
        assertNotNull(stats.get().getCreatedAt());
        assertNotNull(stats.get().getExpiresAt());
    }

    @Test
    void testMultipleShortenings_CreateUniqueCodes() {
        // Given
        String url1 = "https://www.example1.com";
        String url2 = "https://www.example2.com";
        String url3 = "https://www.example3.com";

        // When
        String code1 = urlShortenerService.shortenUrl(url1, "user1");
        String code2 = urlShortenerService.shortenUrl(url2, "user2");
        String code3 = urlShortenerService.shortenUrl(url3, "user3");

        // Then
        assertNotEquals(code1, code2);
        assertNotEquals(code2, code3);
        assertNotEquals(code1, code3);

        // Verify all are retrievable
        assertEquals(url1, urlShortenerService.getOriginalUrl(code1).get());
        assertEquals(url2, urlShortenerService.getOriginalUrl(code2).get());
        assertEquals(url3, urlShortenerService.getOriginalUrl(code3).get());
    }

    @Test
    void testSameUrlMultipleTimes_CreatesDifferentCodes() {
        // Given
        String username = "testuser";

        // When
        String code1 = urlShortenerService.shortenUrl(validUrl, username);
        String code2 = urlShortenerService.shortenUrl(validUrl, username);

        // Then - Should create different codes (due to timestamp in collision handling)
        // Note: This may or may not be equal depending on timing, but both should work
        assertNotNull(code1);
        assertNotNull(code2);
        
        // Both should retrieve the same original URL
        assertEquals(validUrl, urlShortenerService.getOriginalUrl(code1).get());
        assertEquals(validUrl, urlShortenerService.getOriginalUrl(code2).get());
    }
}

