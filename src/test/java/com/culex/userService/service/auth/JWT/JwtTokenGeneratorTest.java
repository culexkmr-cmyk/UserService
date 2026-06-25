package com.culex.userService.service.auth.JWT;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.main.allow-bean-definition-overriding=true",
        "app.jwt.access-expiration-ms=3600000",
        "app.jwt.refresh-expiration-ms=86400000"
})
public class JwtTokenGeneratorTest {

    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;

    @Autowired
    private PublicKey publicKey;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public PrivateKey privateKey() throws Exception {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            return keyPair.getPrivate();
        }

        @Bean
        @Primary
        public PublicKey publicKey() throws Exception {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            return keyPair.getPublic();
        }
    }

    @Test
    @DisplayName("generateAccessToken: should generate valid JWT with correct claims")
    void generateAccessToken_ShouldGenerateValidToken() {
        String username = "testUser";
        long userId = 123L;

        String accessToken = jwtTokenGenerator.generateAccessToken(username, userId);

        assertNotNull(accessToken);
        assertFalse(accessToken.isEmpty());

        String[] parts = accessToken.split("\\.");
        assertEquals(3, parts.length, "JWT должен состоять из 3 частей");

        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        assertTrue(payload.contains("\"sub\":\"" + username + "\""), "Токен должен содержать username в subject");
        assertTrue(payload.contains("\"userId\":" + userId), "Токен должен содержать userId в claims");
        assertTrue(payload.contains("\"jti\":"), "Токен должен содержать уникальный jti");
    }

    @Test
    @DisplayName("generateRefreshToken: should return RefreshTokenData with valid token and metadata")
    void generateRefreshToken_ShouldReturnValidData() {
        String username = "testUser";
        long userId = 456L;
        Instant beforeGeneration = Instant.now();

        RefreshTokenData result = jwtTokenGenerator.generateRefreshToken(username, userId);

        assertNotNull(result);
        assertNotNull(result.token());
        assertNotNull(result.jti());
        assertNotNull(result.expiresAt());

        Instant expectedExpiration = beforeGeneration.plus(86400000, ChronoUnit.MILLIS);
        long differenceSeconds = Math.abs(result.expiresAt().getEpochSecond() - expectedExpiration.getEpochSecond());
        assertTrue(differenceSeconds < 5, "Время истечения должно быть примерно через 24 часа");

        assertDoesNotThrow(() -> java.util.UUID.fromString(result.jti()), "jti должен быть валидным UUID");

        String[] parts = result.token().split("\\.");
        assertEquals(3, parts.length, "JWT должен состоять из 3 частей");

        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        assertTrue(payload.contains("\"sub\":\"" + username + "\""));
        assertTrue(payload.contains("\"userId\":" + userId));
        assertTrue(payload.contains("\"jti\":\"" + result.jti() + "\""), "jti в токене должен совпадать с jti в RefreshTokenData");
    }

    @Test
    @DisplayName("generateAccessToken: should generate unique tokens for same user")
    void generateAccessToken_ShouldGenerateUniqueTokens() {
        String username = "testUser";
        long userId = 789L;

        String token1 = jwtTokenGenerator.generateAccessToken(username, userId);
        String token2 = jwtTokenGenerator.generateAccessToken(username, userId);

        assertNotEquals(token1, token2, "Каждый токен должен быть уникальным (разные jti и issuedAt)");
    }

    @Test
    @DisplayName("generateRefreshToken: should generate unique jti for each call")
    void generateRefreshToken_ShouldGenerateUniqueJti() {
        String username = "testUser";
        long userId = 999L;

        RefreshTokenData result1 = jwtTokenGenerator.generateRefreshToken(username, userId);
        RefreshTokenData result2 = jwtTokenGenerator.generateRefreshToken(username, userId);

        assertNotEquals(result1.jti(), result2.jti(), "Каждый refresh token должен иметь уникальный jti");
        assertNotEquals(result1.token(), result2.token(), "Токены должны быть разными");
    }
}