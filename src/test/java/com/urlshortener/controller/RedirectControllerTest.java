package com.urlshortener.controller;

import com.urlshortener.service.UrlShortenerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RedirectController.class)
@AutoConfigureMockMvc(addFilters = false)
class RedirectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlShortenerService urlShortenerService;

    @MockBean
    private com.urlshortener.security.JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void redirect_shouldReturnFoundWithLocation_whenShortCodeExists() throws Exception {
        when(urlShortenerService.getOriginalUrl("abc123")).thenReturn(Optional.of("https://example.com"));

        mockMvc.perform(get("/abc123"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com"));
    }

    @Test
    void redirect_shouldReturnNotFound_whenShortCodeMissing() throws Exception {
        when(urlShortenerService.getOriginalUrl("missing")).thenReturn(Optional.empty());

        mockMvc.perform(get("/missing"))
                .andExpect(status().isNotFound());
    }
}
