package com.urlshortener.repository;

import com.urlshortener.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void save_and_findByUsername_and_findByEmail_and_exists_checks() {
        User user = new User("alice", "alice@example.com", "secret");
        user = userRepository.save(user);

        Optional<User> byUsername = userRepository.findByUsername("alice");
        Optional<User> byEmail = userRepository.findByEmail("alice@example.com");

        assertThat(byUsername).isPresent();
        assertThat(byUsername.get().getId()).isEqualTo(user.getId());

        assertThat(byEmail).isPresent();
        assertThat(byEmail.get().getUsername()).isEqualTo("alice");

        assertThat(userRepository.existsByUsername("alice")).isTrue();
        assertThat(userRepository.existsByEmail("alice@example.com")).isTrue();
    }
}
