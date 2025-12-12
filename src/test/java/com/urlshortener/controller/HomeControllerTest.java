package com.urlshortener.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HomeControllerTest {

    private final HomeController controller = new HomeController();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void home_WhenAuthenticated_AddsUsername() {
        var authentication = new UsernamePasswordAuthenticationToken("alice", null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Model model = new ExtendedModelMap();

        String view = controller.home(model);

        assertEquals("index", view);
        assertEquals("alice", model.getAttribute("username"));
    }

    @Test
    void home_WhenAnonymous_DoesNotAddUsername() {
        Model model = new ExtendedModelMap();

        String view = controller.home(model);

        assertEquals("index", view);
        assertNull(model.getAttribute("username"));
    }
}
