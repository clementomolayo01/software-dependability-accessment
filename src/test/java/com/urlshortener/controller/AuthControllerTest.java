package com.urlshortener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urlshortener.dto.AuthRequest;
import com.urlshortener.security.JwtTokenProvider;
import com.urlshortener.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider tokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testLogin_ValidCredentials_ReturnsToken() throws Exception {
        // Given
        AuthRequest request = new AuthRequest("testuser", "password123");
        String token = "test-token";
        
        when(userService.validateCredentials("testuser", "password123")).thenReturn(true);
        when(tokenProvider.generateToken("testuser")).thenReturn(token);

        // When/Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void testLogin_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        // Given
        AuthRequest request = new AuthRequest("testuser", "wrongpassword");
        when(userService.validateCredentials("testuser", "wrongpassword")).thenReturn(false);

        // When/Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRegister_ValidRequest_CreatesUser() throws Exception {
        // Given
        AuthRequest request = new AuthRequest("newuser", "password123");
        doNothing().when(userService).createUser(anyString(), anyString(), anyString());

        // When/Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        
        verify(userService, times(1)).createUser(anyString(), anyString(), anyString());
    }

    @Test
    void testRegister_DuplicateUsername_ReturnsBadRequest() throws Exception {
        // Given
        AuthRequest request = new AuthRequest("existinguser", "password123");
        doThrow(new IllegalArgumentException("Username already exists"))
            .when(userService).createUser(anyString(), anyString(), anyString());

        // When/Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

