package com.urlshortener.service;

import com.urlshortener.entity.User;
import com.urlshortener.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new user.
     * 
     * @param username The username (must be unique)
     * @param email The email (must be unique)
     * @param password The plain text password
     * @return The created user
     * 
     * @requires username != null && !username.isEmpty()
     * @requires email != null && !email.isEmpty() && isValidEmail(email)
     * @requires password != null && !password.isEmpty()
     * @requires !userRepository.existsByUsername(username)
     * @requires !userRepository.existsByEmail(email)
     * @ensures \result != null
     * @ensures userRepository.findByUsername(username).isPresent()
     */
    public User createUser(String username, String email, String password) {
        //@ assert username != null && !username.isEmpty();
        //@ assert email != null && !email.isEmpty();
        //@ assert password != null && !password.isEmpty();
        
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(username, email, encodedPassword);
        User saved = userRepository.save(user);
        
        //@ assert saved != null;
        //@ assert userRepository.findByUsername(username).isPresent();
        return saved;
    }

    /**
     * Finds a user by username.
     * 
     * @param username The username to search for
     * @return Optional containing the user if found
     * 
     * @requires username != null && !username.isEmpty()
     * @ensures \result != null
     */
    public Optional<User> findByUsername(String username) {
        //@ assert username != null && !username.isEmpty();
        //@ assert \result != null;
        return userRepository.findByUsername(username);
    }

    /**
     * Validates user credentials.
     * 
     * @param username The username
     * @param password The plain text password
     * @return true if credentials are valid, false otherwise
     * 
     * @requires username != null && !username.isEmpty()
     * @requires password != null && !password.isEmpty()
     * @ensures \result == (user exists and password matches)
     */
    public boolean validateCredentials(String username, String password) {
        //@ assert username != null && !username.isEmpty();
        //@ assert password != null && !password.isEmpty();
        
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        boolean result = passwordEncoder.matches(password, user.getPassword()) && user.getEnabled();
        
        //@ assert \result == (userOpt.isPresent() && passwordEncoder.matches(password, user.getPassword()) && user.getEnabled());
        return result;
    }
}

