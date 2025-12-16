package com.urlshortener.config;

import com.urlshortener.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class SecurityConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Test
    void passwordEncoder_and_securityFilterChain_areProvided() {
        assertThat(passwordEncoder).isNotNull();

        String encoded = passwordEncoder.encode("topSecret");
        assertThat(passwordEncoder.matches("topSecret", encoded)).isTrue();

        assertThat(securityFilterChain).isNotNull();
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter() {
            // Provide a plain instance to satisfy SecurityConfig autowiring
            return new JwtAuthenticationFilter();
        }
    }
}
