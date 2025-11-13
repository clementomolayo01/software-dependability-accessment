package com.urlshortener.service;

import com.urlshortener.entity.ShortUrl;
import com.urlshortener.repository.ShortUrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceTest {

    @Mock
    private ShortUrlRepository shortUrlRepository;

    @InjectMocks
    private UrlShortenerService urlShortenerService;

    private String validUrl;
    private String invalidUrl;

    @BeforeEach
    void setUp() {
        validUrl = "https://www.example.com";
        invalidUrl = "not-a-valid-url";
    }

    @Test
    void testShortenUrl_ValidUrl_ReturnsShortCode() {
        // Given
        String username = "testuser";
        when(shortUrlRepository.findByShortCode(anyString())).thenReturn(Optional.empty());
        when(shortUrlRepository.save(any(ShortUrl.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        String shortCode = urlShortenerService.shortenUrl(validUrl, username);

        // Then
        assertNotNull(shortCode);
        assertEquals(8, shortCode.length());
        verify(shortUrlRepository, times(1)).save(any(ShortUrl.class));
    }

    @Test
    void testShortenUrl_NullUrl_ThrowsException() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            urlShortenerService.shortenUrl(null, "user");
        });
    }

    @Test
    void testShortenUrl_EmptyUrl_ThrowsException() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            urlShortenerService.shortenUrl("", "user");
        });
    }

    @Test
    void testShortenUrl_InvalidUrl_ThrowsException() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            urlShortenerService.shortenUrl(invalidUrl, "user");
        });
    }

    @Test
    void testShortenUrl_HandlesCollision() {
        // Given
        String username = "testuser";
        ShortUrl existing = new ShortUrl("ABCD1234", validUrl, username);
        
        when(shortUrlRepository.findByShortCode(anyString()))
            .thenReturn(Optional.of(existing))
            .thenReturn(Optional.empty());
        when(shortUrlRepository.save(any(ShortUrl.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        String shortCode = urlShortenerService.shortenUrl(validUrl, username);

        // Then
        assertNotNull(shortCode);
        verify(shortUrlRepository, atLeast(1)).findByShortCode(anyString());
    }

    @Test
    void testGetOriginalUrl_ValidCode_ReturnsUrl() {
        // Given
        String shortCode = "ABCD1234";
        ShortUrl shortUrl = new ShortUrl(shortCode, validUrl, "user");
        when(shortUrlRepository.findByShortCode(shortCode)).thenReturn(Optional.of(shortUrl));
        when(shortUrlRepository.save(any(ShortUrl.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Optional<String> result = urlShortenerService.getOriginalUrl(shortCode);

        // Then
        assertTrue(result.isPresent());
        assertEquals(validUrl, result.get());
        verify(shortUrlRepository, times(1)).save(any(ShortUrl.class));
    }

    @Test
    void testGetOriginalUrl_InvalidCode_ReturnsEmpty() {
        // Given
        String shortCode = "INVALID";
        when(shortUrlRepository.findByShortCode(shortCode)).thenReturn(Optional.empty());

        // When
        Optional<String> result = urlShortenerService.getOriginalUrl(shortCode);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testGetOriginalUrl_NullCode_ReturnsEmpty() {
        // When
        Optional<String> result = urlShortenerService.getOriginalUrl(null);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testGetOriginalUrl_EmptyCode_ReturnsEmpty() {
        // When
        Optional<String> result = urlShortenerService.getOriginalUrl("");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testGetStatistics_ValidCode_ReturnsStatistics() {
        // Given
        String shortCode = "ABCD1234";
        ShortUrl shortUrl = new ShortUrl(shortCode, validUrl, "user");
        when(shortUrlRepository.findByShortCode(shortCode)).thenReturn(Optional.of(shortUrl));

        // When
        Optional<ShortUrl> result = urlShortenerService.getStatistics(shortCode);

        // Then
        assertTrue(result.isPresent());
        assertEquals(shortUrl, result.get());
    }

    @Test
    void testGetStatistics_InvalidCode_ReturnsEmpty() {
        // Given
        String shortCode = "INVALID";
        when(shortUrlRepository.findByShortCode(shortCode)).thenReturn(Optional.empty());

        // When
        Optional<ShortUrl> result = urlShortenerService.getStatistics(shortCode);

        // Then
        assertFalse(result.isPresent());
    }
}

