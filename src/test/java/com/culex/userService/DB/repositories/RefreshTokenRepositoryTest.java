package com.culex.userService.DB.repositories;

import com.culex.userService.DB.entities.RefreshToken;
import com.culex.userService.DB.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;


import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("RefreshTokenRepository Integration Tests")
public class RefreshTokenRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private User testUser;
    private RefreshToken testToken;

    @BeforeEach
    void setUp() {
        testUser = new User("password123", "testuser", "TestNick", "test@example.com");
        entityManager.persist(testUser);

        testToken = new RefreshToken("unique-jti-123", Instant.now().plusSeconds(3600), testUser);
        entityManager.persist(testToken);
        entityManager.flush();
    }

    @Test
    @DisplayName("findByJti: should return token when jti exists")
    void findByJti_ExistingJti_ShouldReturnToken() {
        Optional<RefreshToken> found = refreshTokenRepository.findByJti("unique-jti-123");

        assertTrue(found.isPresent());
        assertEquals("unique-jti-123", found.get().getJti());
        assertEquals(testUser.getId(), found.get().getUser().getId());
    }

    @Test
    @DisplayName("findByJti: should return empty when jti does not exist")
    void findByJti_NonExistingJti_ShouldReturnEmpty() {
        Optional<RefreshToken> found = refreshTokenRepository.findByJti("nonexistent-jti");

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("findAllByUser: should return all tokens for user")
    void findAllByUser_ExistingUser_ShouldReturnTokens() {
        RefreshToken secondToken = new RefreshToken("second-jti-456", Instant.now().plusSeconds(7200), testUser);
        entityManager.persist(secondToken);
        entityManager.flush();

        List<RefreshToken> tokens = refreshTokenRepository.findAllByUser(testUser);

        assertEquals(2, tokens.size());
        assertTrue(tokens.stream().anyMatch(t -> t.getJti().equals("unique-jti-123")));
        assertTrue(tokens.stream().anyMatch(t -> t.getJti().equals("second-jti-456")));
    }

    @Test
    @DisplayName("findAllByUser: should return empty list when user has no tokens")
    void findAllByUser_UserWithNoTokens_ShouldReturnEmptyList() {
        User newUser = new User("pass456", "newuser", "NewNick", "new@example.com");
        entityManager.persist(newUser);
        entityManager.flush();

        List<RefreshToken> tokens = refreshTokenRepository.findAllByUser(newUser);

        assertTrue(tokens.isEmpty());
    }

    @Test
    @DisplayName("deleteByJti: should delete token by jti")
    void deleteByJti_ExistingJti_ShouldDeleteToken() {
        refreshTokenRepository.deleteByJti("unique-jti-123");
        entityManager.flush();

        Optional<RefreshToken> found = refreshTokenRepository.findByJti("unique-jti-123");
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("deleteAllByUser: should delete all tokens for user")
    void deleteAllByUser_ExistingUser_ShouldDeleteAllTokens() {
        RefreshToken secondToken = new RefreshToken("second-jti-456", Instant.now().plusSeconds(7200), testUser);
        entityManager.persist(secondToken);
        entityManager.flush();

        refreshTokenRepository.deleteAllByUser(testUser);
        entityManager.flush();

        List<RefreshToken> tokens = refreshTokenRepository.findAllByUser(testUser);
        assertTrue(tokens.isEmpty());
    }

    @Test
    @DisplayName("save: should persist token with generated ID")
    void save_ValidToken_ShouldPersistWithId() {
        RefreshToken newToken = new RefreshToken("new-jti-789", Instant.now().plusSeconds(1800), testUser);

        RefreshToken saved = refreshTokenRepository.save(newToken);

        assertNotNull(saved.getJti());
        assertEquals("new-jti-789", saved.getJti());
    }
}