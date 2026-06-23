package com.culex.userService.service.auth.JWT;

import com.culex.userService.DB.entities.RefreshToken;
import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.RefreshTokenRepository;
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
public class TokenUpdaterTest {

    @MockitoBean
    private JwtTokenGenerator jwtTokenGenerator;

    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;

    @MockitoBean
    private TokenUpdateValidation validation;

    @Autowired
    private TokenUpdater tokenUpdater;

    @Test
    @DisplayName("updateToken: should validate, delete old token, generate and save new tokens")
    void updateToken_ValidData_ShouldRotateTokens() {
        Long userId = 1L;
        String oldJti = "old-jti-123";
        String username = "validUser";

        User mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn(username);

        RefreshToken oldRefreshToken = mock(RefreshToken.class);

        TokenUpdateValidation.ValidationResponse mockValidationResponse = mock(TokenUpdateValidation.ValidationResponse.class);
        when(mockValidationResponse.user()).thenReturn(mockUser);
        when(mockValidationResponse.refreshToken()).thenReturn(oldRefreshToken);
        when(validation.allValidation(oldJti, userId)).thenReturn(mockValidationResponse);

        String newJti = "new-jti-456";
        Instant newExpiresAt = Instant.now().plusSeconds(86400);
        String newRefreshTokenStr = "new-refresh-token-jwt";
        
        JwtTokenGenerator.RefreshTokenData mockRefreshTokenData = mock(JwtTokenGenerator.RefreshTokenData.class);
        when(mockRefreshTokenData.jti()).thenReturn(newJti);
        when(mockRefreshTokenData.expiresAt()).thenReturn(newExpiresAt);
        when(mockRefreshTokenData.token()).thenReturn(newRefreshTokenStr);
        
        when(jwtTokenGenerator.generateRefreshToken(username, userId)).thenReturn(mockRefreshTokenData);

        String newAccessToken = "new-access-token-jwt";
        when(jwtTokenGenerator.generateAccessToken(username, userId)).thenReturn(newAccessToken);

        UpdateResponse response = tokenUpdater.updateToken(userId, oldJti);

        assertEquals(newAccessToken, response.accessToken());
        assertEquals(newRefreshTokenStr, response.refreshToken());

        verify(validation).allValidation(oldJti, userId);
        verify(jwtTokenGenerator).generateRefreshToken(username, userId);
        verify(jwtTokenGenerator).generateAccessToken(username, userId);

        verify(refreshTokenRepository).delete(oldRefreshToken);

        ArgumentCaptor<RefreshToken> tokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(tokenCaptor.capture());

        RefreshToken capturedToken = tokenCaptor.getValue();
        assertEquals(newJti, capturedToken.getJti());
        assertEquals(newExpiresAt, capturedToken.getExpiresAt());
        assertSame(mockUser, capturedToken.getUser());
    }

    @Test
    @DisplayName("updateToken: should throw exception and NOT modify tokens if validation fails")
    void updateToken_InvalidData_ShouldThrowAndNotModifyTokens() {
        Long userId = 1L;
        String invalidJti = "invalid-jti";

        when(validation.allValidation(invalidJti, userId))
                .thenThrow(new BadCredentialsException("Invalid refresh token"));

        assertThrows(BadCredentialsException.class, () -> {
            tokenUpdater.updateToken(userId, invalidJti);
        });

        verify(jwtTokenGenerator, never()).generateRefreshToken(anyString(), anyLong());
        verify(jwtTokenGenerator, never()).generateAccessToken(anyString(), anyLong());
        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }
}