package com.culex.userService.service.auth.login;

import com.culex.userService.DB.entities.RefreshToken;
import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.RefreshTokenRepository;
import com.culex.userService.service.auth.JWT.JwtTokenGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class LoginServiceTest {

    @MockitoBean
    private JwtTokenGenerator tokenGenerator;

    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;

    @MockitoBean
    private LoginValidation loginValidation;

    @Autowired
    private LoginService loginService;

    @Test
    @DisplayName("login: should validate, generate tokens, save refresh token and return LoginResponse")
    void login_ValidCredentials_ShouldReturnTokensAndSaveRefreshToken() {
        String password = "SecurePass123!";
        String username = "validUser";
        Long userId = 1L;

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);
        when(mockUser.getUsername()).thenReturn(username);

        when(loginValidation.allValidation(password, username)).thenReturn(mockUser);

        String expectedAccessToken = "mock-access-token-jwt";
        when(tokenGenerator.generateAccessToken(username, userId)).thenReturn(expectedAccessToken);

        String jti = "unique-jti-12345";
        Instant expiresAt = Instant.now().plusSeconds(86400);
        String expectedRefreshToken = "mock-refresh-token-jwt";

        JwtTokenGenerator.RefreshTokenData mockRefreshTokenData = mock(JwtTokenGenerator.RefreshTokenData.class);
        when(mockRefreshTokenData.jti()).thenReturn(jti);
        when(mockRefreshTokenData.expiresAt()).thenReturn(expiresAt);
        when(mockRefreshTokenData.token()).thenReturn(expectedRefreshToken);

        when(tokenGenerator.generateRefreshToken(username, userId)).thenReturn(mockRefreshTokenData);

        LoginResponse response = loginService.login(password, username);

        assertEquals(expectedAccessToken, response.accessToken());
        assertEquals(expectedRefreshToken, response.refreshToken());

        verify(loginValidation).allValidation(password, username);
        verify(tokenGenerator).generateAccessToken(username, userId);
        verify(tokenGenerator).generateRefreshToken(username, userId);

        ArgumentCaptor<RefreshToken> tokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(tokenCaptor.capture());

        RefreshToken capturedToken = tokenCaptor.getValue();
        assertEquals(jti, capturedToken.getJti());
        assertEquals(expiresAt, capturedToken.getExpiresAt());
        assertSame(mockUser, capturedToken.getUser());
    }

    @Test
    @DisplayName("login: should throw exception and NOT generate tokens if validation fails")
    void login_InvalidCredentials_ShouldThrowAndNotGenerateTokens() {
        String password = "wrongPassword";
        String username = "invalidUser";

        when(loginValidation.allValidation(password, username))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> {
            loginService.login(password, username);
        });

        verify(tokenGenerator, never()).generateAccessToken(anyString(), anyLong());
        verify(tokenGenerator, never()).generateRefreshToken(anyString(), anyLong());
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }
}