package com.urlshortener.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    private String secret = "test-secret-key-for-testing-purposes-only-minimum-32-characters";
    private long expiration = 86400000L; // 24 hours

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", secret);
        ReflectionTestUtils.setField(tokenProvider, "jwtExpiration", expiration);
    }

    @Test
    void testGenerateToken_ValidUsername_ReturnsToken() {
        // Given
        String username = "testuser";

        // When
        String token = tokenProvider.generateToken(username);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testGetUsernameFromToken_ValidToken_ReturnsUsername() {
        // Given
        String username = "testuser";
        String token = tokenProvider.generateToken(username);

        // When
        String extractedUsername = tokenProvider.getUsernameFromToken(token);

        // Then
        assertEquals(username, extractedUsername);
    }

    @Test
    void testValidateToken_ValidToken_ReturnsTrue() {
        // Given
        String username = "testuser";
        String token = tokenProvider.generateToken(username);

        // When
        boolean isValid = tokenProvider.validateToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidToken_ReturnsFalse() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        boolean isValid = tokenProvider.validateToken(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_NullToken_ReturnsFalse() {
        // When
        boolean isValid = tokenProvider.validateToken(null);

        // Then
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_EmptyToken_ReturnsFalse() {
        // When
        boolean isValid = tokenProvider.validateToken("");

        // Then
        assertFalse(isValid);
    }
}

