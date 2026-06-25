package com.culex.userService.DB.repositories;

import com.culex.userService.DB.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("UserRepository Integration Tests")
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("password123", "testuser", "TestNick", "test@example.com");
        entityManager.persist(testUser);
        entityManager.flush();
    }

    @Test
    @DisplayName("findByUsername: should return user when username exists")
    void findByUsername_ExistingUsername_ShouldReturnUser() {
        Optional<User> found = userRepository.findByUsername("testuser");

        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    @DisplayName("findByUsername: should return empty when username does not exist")
    void findByUsername_NonExistingUsername_ShouldReturnEmpty() {
        Optional<User> found = userRepository.findByUsername("nonexistent");

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("findByEmail: should return user when email exists")
    void findByEmail_ExistingEmail_ShouldReturnUser() {
        Optional<User> found = userRepository.findByEmail("test@example.com");

        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    @DisplayName("findByEmail: should return empty when email does not exist")
    void findByEmail_NonExistingEmail_ShouldReturnEmpty() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("save: should persist user with generated ID")
    void save_ValidUser_ShouldPersistWithId() {
        User newUser = new User("pass456", "newuser", "NewNick", "new@example.com");
        
        User saved = userRepository.save(newUser);
        
        assertNotNull(saved.getId());
        assertEquals("newuser", saved.getUsername());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    @DisplayName("save: should enforce unique username constraint")
    void save_DuplicateUsername_ShouldThrowException() {
        User duplicateUser = new User("pass789", "testuser", "AnotherNick", "another@example.com");
        
        assertThrows(Exception.class, () -> {
            userRepository.save(duplicateUser);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("save: should enforce unique email constraint")
    void save_DuplicateEmail_ShouldThrowException() {
        User duplicateUser = new User("pass789", "anotheruser", "TestNick", "test@example.com");
        
        assertThrows(Exception.class, () -> {
            userRepository.save(duplicateUser);
            entityManager.flush();
        });
    }
}