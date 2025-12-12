package com.urlshortener.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    @Test
    void constructor_SetsFieldsAndDefaults() {
        User user = new User("alice", "alice@example.com", "secret");

        assertEquals("alice", user.getUsername());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals("secret", user.getPassword());
        assertNotNull(user.getCreatedAt());
        assertTrue(user.getEnabled());
    }

    @Test
    void setters_UpdateValues() {
        User user = new User();
        LocalDateTime now = LocalDateTime.now();

        user.setId(10L);
        user.setUsername("bob");
        user.setEmail("bob@example.com");
        user.setPassword("pwd");
        user.setCreatedAt(now);
        user.setEnabled(false);

        assertEquals(10L, user.getId());
        assertEquals("bob", user.getUsername());
        assertEquals("bob@example.com", user.getEmail());
        assertEquals("pwd", user.getPassword());
        assertEquals(now, user.getCreatedAt());
        assertFalse(user.getEnabled());
    }
}
