package com.urlshortener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.entity.ShortUrl;
import com.urlshortener.service.UrlShortenerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlShortenerService urlShortenerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testShortenUrl_ValidRequest_ReturnsShortUrl() throws Exception {
        // Given
        ShortenUrlRequest request = new ShortenUrlRequest("https://www.example.com");
        String shortCode = "ABCD1234";
        when(urlShortenerService.shortenUrl(anyString(), any())).thenReturn(shortCode);

        // When/Then
        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").value(shortCode))
                .andExpect(jsonPath("$.originalUrl").value("https://www.example.com"));
    }

    @Test
    void testShortenUrl_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Given
        ShortenUrlRequest request = new ShortenUrlRequest("invalid-url");

        // When/Then
        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetStatistics_ValidCode_ReturnsStatistics() throws Exception {
        // Given
        String shortCode = "ABCD1234";
        ShortUrl shortUrl = new ShortUrl(shortCode, "https://www.example.com", "user");
        shortUrl.setClickCount(10L);
        shortUrl.setCreatedAt(LocalDateTime.now());
        shortUrl.setExpiresAt(LocalDateTime.now().plusYears(1));
        
        when(urlShortenerService.getStatistics(shortCode)).thenReturn(Optional.of(shortUrl));

        // When/Then
        mockMvc.perform(get("/api/stats/{shortCode}", shortCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").value(shortCode))
                .andExpect(jsonPath("$.clickCount").value(10));
    }

    @Test
    void testGetStatistics_InvalidCode_ReturnsNotFound() throws Exception {
        // Given
        String shortCode = "INVALID";
        when(urlShortenerService.getStatistics(shortCode)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/api/stats/{shortCode}", shortCode))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRedirect_ValidCode_Redirects() throws Exception {
        // Given
        String shortCode = "ABCD1234";
        String originalUrl = "https://www.example.com";
        when(urlShortenerService.getOriginalUrl(shortCode)).thenReturn(Optional.of(originalUrl));

        // When/Then
        mockMvc.perform(get("/{shortCode}", shortCode))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", originalUrl));
    }

    @Test
    void testRedirect_InvalidCode_ReturnsNotFound() throws Exception {
        // Given
        String shortCode = "INVALID";
        when(urlShortenerService.getOriginalUrl(shortCode)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/{shortCode}", shortCode))
                .andExpect(status().isNotFound());
    }
}

