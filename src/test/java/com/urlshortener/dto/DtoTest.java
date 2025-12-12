package com.urlshortener.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DtoTest {

    @Test
    void shortenUrlResponse_GettersAndSettersWork() {
        ShortenUrlResponse response = new ShortenUrlResponse();
        response.setShortUrl("http://localhost/abc");
        response.setShortCode("abc");
        response.setOriginalUrl("https://example.com");

        assertEquals("http://localhost/abc", response.getShortUrl());
        assertEquals("abc", response.getShortCode());
        assertEquals("https://example.com", response.getOriginalUrl());
    }

    @Test
    void authResponse_DefaultTypeIsBearer() {
        AuthResponse response = new AuthResponse("token123", "bob");
        assertEquals("token123", response.getToken());
        assertEquals("bob", response.getUsername());
        assertEquals("Bearer", response.getType());

        response.setType("Custom");
        response.setToken("newToken");
        response.setUsername("alice");

        assertEquals("Custom", response.getType());
        assertEquals("newToken", response.getToken());
        assertEquals("alice", response.getUsername());
    }

    @Test
    void authResponse_NoArgsConstructorAllowsSetters() {
        AuthResponse response = new AuthResponse();
        assertEquals("Bearer", response.getType());

        response.setToken("t");
        response.setUsername("u");

        assertEquals("t", response.getToken());
        assertEquals("u", response.getUsername());
    }

    @Test
    void statisticsResponse_HoldsProvidedValues() {
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime expires = created.plusDays(1);
        StatisticsResponse response = new StatisticsResponse("code", "url", "short", 5L, created, expires);

        assertEquals("code", response.getShortCode());
        assertEquals("url", response.getOriginalUrl());
        assertEquals("short", response.getShortUrl());
        assertEquals(5L, response.getClickCount());
        assertEquals(created, response.getCreatedAt());
        assertEquals(expires, response.getExpiresAt());
    }

    @Test
    void statisticsResponse_SettersUpdateValues() {
        StatisticsResponse response = new StatisticsResponse();
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime expires = created.plusHours(2);

        response.setShortCode("xyz");
        response.setOriginalUrl("http://example.com");
        response.setShortUrl("http://short");
        response.setClickCount(10L);
        response.setCreatedAt(created);
        response.setExpiresAt(expires);

        assertEquals("xyz", response.getShortCode());
        assertEquals("http://example.com", response.getOriginalUrl());
        assertEquals("http://short", response.getShortUrl());
        assertEquals(10L, response.getClickCount());
        assertEquals(created, response.getCreatedAt());
        assertEquals(expires, response.getExpiresAt());
    }
}
