package com.urlshortener.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ShortUrlTest {

    @Test
    void constructor_SetsDefaults() {
        ShortUrl shortUrl = new ShortUrl("ABC12345", "https://example.com", "creator");

        assertEquals("ABC12345", shortUrl.getShortCode());
        assertEquals("https://example.com", shortUrl.getOriginalUrl());
        assertEquals("creator", shortUrl.getCreatedBy());
        assertNotNull(shortUrl.getCreatedAt());
        assertNotNull(shortUrl.getExpiresAt());
        assertEquals(0L, shortUrl.getClickCount());
    }

    @Test
    void incrementClickCount_IncreasesByOne() {
        ShortUrl shortUrl = new ShortUrl("ABC12345", "https://example.com", "creator");
        shortUrl.setClickCount(5L);

        shortUrl.incrementClickCount();

        assertEquals(6L, shortUrl.getClickCount());
    }

    @Test
    void isExpired_ReturnsTrueWhenPastExpiry() {
        ShortUrl shortUrl = new ShortUrl("ABC12345", "https://example.com", "creator");
        shortUrl.setExpiresAt(LocalDateTime.now().minusDays(1));

        assertTrue(shortUrl.isExpired());
    }

    @Test
    void isExpired_ReturnsFalseWhenNotPastExpiry() {
        ShortUrl shortUrl = new ShortUrl("ABC12345", "https://example.com", "creator");
        shortUrl.setExpiresAt(LocalDateTime.now().plusDays(1));

        assertFalse(shortUrl.isExpired());
    }

    @Test
    void setters_UpdateAllFields() {
        ShortUrl shortUrl = new ShortUrl();
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime expires = created.plusDays(10);

        shortUrl.setId(15L);
        shortUrl.setShortCode("ZZZZ9999");
        shortUrl.setOriginalUrl("http://updated.com");
        shortUrl.setCreatedAt(created);
        shortUrl.setExpiresAt(expires);
        shortUrl.setClickCount(42L);
        shortUrl.setCreatedBy("owner");

        assertEquals(15L, shortUrl.getId());
        assertEquals("ZZZZ9999", shortUrl.getShortCode());
        assertEquals("http://updated.com", shortUrl.getOriginalUrl());
        assertEquals(created, shortUrl.getCreatedAt());
        assertEquals(expires, shortUrl.getExpiresAt());
        assertEquals(42L, shortUrl.getClickCount());
        assertEquals("owner", shortUrl.getCreatedBy());
    }
}
