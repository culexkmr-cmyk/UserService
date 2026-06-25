package com.culex.userService.service.auth.login;

import com.culex.userService.DB.entities.RefreshToken;
import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.RefreshTokenRepository;
import com.culex.userService.DB.repositories.UserRepository;
import com.culex.userService.service.auth.JWT.JwtTokenGenerator;
import com.culex.userService.service.auth.JWT.RefreshTokenData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.Optional;

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

    @MockitoBean
    private UserRepository userRepository;

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

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        doNothing().when(loginValidation).allValidation(password, mockUser);

        String expectedAccessToken = "mock-access-token-jwt";
        when(tokenGenerator.generateAccessToken(username, userId)).thenReturn(expectedAccessToken);

        String jti = "unique-jti-12345";
        Instant expiresAt = Instant.now().plusSeconds(86400);
        String expectedRefreshToken = "mock-refresh-token-jwt";

        RefreshTokenData mockRefreshTokenData = mock(RefreshTokenData.class);
        when(mockRefreshTokenData.jti()).thenReturn(jti);
        when(mockRefreshTokenData.expiresAt()).thenReturn(expiresAt);
        when(mockRefreshTokenData.token()).thenReturn(expectedRefreshToken);

        when(tokenGenerator.generateRefreshToken(username, userId)).thenReturn(mockRefreshTokenData);

        LoginResponse response = loginService.login(password, username);

        assertEquals(expectedAccessToken, response.accessToken());
        assertEquals(expectedRefreshToken, response.refreshToken());

        verify(userRepository).findByUsername(username);
        verify(loginValidation).allValidation(password, mockUser);
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

        User mockUser = mock(User.class);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        doThrow(new BadCredentialsException("Invalid credentials"))
                .when(loginValidation).allValidation(password, mockUser);

        assertThrows(BadCredentialsException.class, () -> loginService.login(password, username));

        verify(userRepository).findByUsername(username);
        verify(loginValidation).allValidation(password, mockUser);
        verify(tokenGenerator, never()).generateAccessToken(anyString(), anyLong());
        verify(tokenGenerator, never()).generateRefreshToken(anyString(), anyLong());
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("login: should throw UsernameNotFoundException if user not found")
    void login_UserNotFound_ShouldThrow() {
        String password = "any";
        String username = "unknown";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> loginService.login(password, username));

        verify(userRepository).findByUsername(username);
        verify(loginValidation, never()).allValidation(anyString(), any());
        verify(tokenGenerator, never()).generateAccessToken(anyString(), anyLong());
        verify(tokenGenerator, never()).generateRefreshToken(anyString(), anyLong());
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }
}