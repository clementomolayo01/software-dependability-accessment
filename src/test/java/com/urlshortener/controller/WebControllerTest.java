package com.urlshortener.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WebControllerTest {

    private final WebController controller = new WebController();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void login_ReturnsLoginView() {
        assertEquals("login", controller.login());
    }

    @Test
    void register_ReturnsRegisterView() {
        assertEquals("register", controller.register());
    }

    @Test
    void dashboard_WhenAuthenticated_AddsUsername() {
        var authentication = new UsernamePasswordAuthenticationToken("bob", null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Model model = new ExtendedModelMap();

        String view = controller.dashboard(model);

        assertEquals("dashboard", view);
        assertEquals("bob", model.getAttribute("username"));
        assertNull(model.getAttribute("token"));
    }

    @Test
    void dashboard_WhenAnonymous_AddsNullUsername() {
        Model model = new ExtendedModelMap();

        String view = controller.dashboard(model);

        assertEquals("dashboard", view);
        assertNull(model.getAttribute("username"));
        assertNull(model.getAttribute("token"));
    }

    @Test
    void stats_PopulatesModelAndReturnsView() {
        Model model = new ExtendedModelMap();

        String view = controller.stats("ABC123", model);

        assertEquals("stats", view);
        assertEquals("ABC123", model.getAttribute("shortCode"));
        assertNull(model.getAttribute("token"));
    }

    @Test
   void dashboard_WhenMockAuthenticated_SetsUsername() {
       Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.isAuthenticated()).thenReturn(true);
       Mockito.when(auth.getName()).thenReturn("alice");
       SecurityContextHolder.getContext().setAuthentication(auth);

       Model model = new ExtendedModelMap();
        String view = controller.dashboard(model);

       assertEquals("dashboard", view);
        assertEquals("alice", model.getAttribute("username"));
       assertNull(model.getAttribute("token"));
    }

}
