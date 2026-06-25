package com.culex.userService.DB.repositories;

import com.culex.userService.DB.entities.PasswordResetToken;
import com.culex.userService.DB.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("PasswordResetTokenRepository Integration Tests")
public class PasswordResetTokenRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    private User testUser;
    private PasswordResetToken testToken;

    @BeforeEach
    void setUp() {
        testUser = new User("password123", "testuser", "TestNick", "test@example.com");
        entityManager.persist(testUser);

        testToken = new PasswordResetToken("unique-token-123", testUser, Instant.now().plusSeconds(3600));
        entityManager.persist(testToken);
        entityManager.flush();
    }

    @Test
    @DisplayName("findByToken: should return token when token exists")
    void findByToken_ExistingToken_ShouldReturnToken() {
        Optional<PasswordResetToken> found = passwordResetTokenRepository.findByToken("unique-token-123");

        assertTrue(found.isPresent());
        assertEquals("unique-token-123", found.get().getToken());
        assertEquals(testUser.getId(), found.get().getUser().getId());
    }

    @Test
    @DisplayName("findByToken: should return empty when token does not exist")
    void findByToken_NonExistingToken_ShouldReturnEmpty() {
        Optional<PasswordResetToken> found = passwordResetTokenRepository.findByToken("nonexistent-token");

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("deleteAllByUser: should delete all tokens for user")
    void deleteAllByUser_ExistingUser_ShouldDeleteAllTokens() {
        PasswordResetToken secondToken = new PasswordResetToken("second-token-456", testUser, Instant.now().plusSeconds(7200));
        entityManager.persist(secondToken);
        entityManager.flush();

        passwordResetTokenRepository.deleteAllByUser(testUser);
        entityManager.flush();
        entityManager.clear();

        Optional<PasswordResetToken> found1 = passwordResetTokenRepository.findByToken("unique-token-123");
        Optional<PasswordResetToken> found2 = passwordResetTokenRepository.findByToken("second-token-456");

        assertFalse(found1.isPresent());
        assertFalse(found2.isPresent());
    }

    @Test
    @DisplayName("save: should persist token")
    void save_ValidToken_ShouldPersist() {
        PasswordResetToken newToken = new PasswordResetToken("new-token-789", testUser, Instant.now().plusSeconds(1800));

        PasswordResetToken saved = passwordResetTokenRepository.save(newToken);
        entityManager.flush();
        entityManager.clear();

        Optional<PasswordResetToken> found = passwordResetTokenRepository.findByToken("new-token-789");
        assertTrue(found.isPresent());
        assertEquals("new-token-789", found.get().getToken());
        assertEquals(testUser.getId(), found.get().getUser().getId());
    }
}