package com.urlshortener.service;

import com.urlshortener.entity.User;
import com.urlshortener.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private String username;
    private String email;
    private String password;

    @BeforeEach
    void setUp() {
        username = "testuser";
        email = "test@example.com";
        password = "password123";
    }

    @Test
    void testCreateUser_ValidInput_CreatesUser() {
        // Given
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User user = userService.createUser(username, email, password);

        // Then
        assertNotNull(user);
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_UsernameExists_ThrowsException() {
        // Given
        when(userRepository.existsByUsername(username)).thenReturn(true);

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(username, email, password);
        });
    }

    @Test
    void testCreateUser_EmailExists_ThrowsException() {
        // Given
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(username, email, password);
        });
    }

    @Test
    void testFindByUsername_UserExists_ReturnsUser() {
        // Given
        User user = new User(username, email, "encodedPassword");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.findByUsername(username);

        // Then
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void testFindByUsername_UserNotExists_ReturnsEmpty() {
        // Given
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.findByUsername(username);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testValidateCredentials_ValidCredentials_ReturnsTrue() {
        // Given
        User user = new User(username, email, "encodedPassword");
        user.setEnabled(true);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);

        // When
        boolean result = userService.validateCredentials(username, password);

        // Then
        assertTrue(result);
    }

    @Test
    void testValidateCredentials_InvalidPassword_ReturnsFalse() {
        // Given
        User user = new User(username, email, "encodedPassword");
        user.setEnabled(true);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(false);

        // When
        boolean result = userService.validateCredentials(username, password);

        // Then
        assertFalse(result);
    }

    @Test
    void testValidateCredentials_UserNotExists_ReturnsFalse() {
        // Given
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When
        boolean result = userService.validateCredentials(username, password);

        // Then
        assertFalse(result);
    }

    @Test
    void testValidateCredentials_UserDisabled_ReturnsFalse() {
        // Given
        User user = new User(username, email, "encodedPassword");
        user.setEnabled(false);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);

        // When
        boolean result = userService.validateCredentials(username, password);

        // Then
        assertFalse(result);
    }
}

