package com.culex.userService.service.auth.logout;

import com.culex.userService.DB.entities.RefreshToken;
import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.RefreshTokenRepository;
import com.culex.userService.DB.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
public class LogoutServiceTest {

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;

    @MockitoBean
    private LogoutValidation logoutValidation;

    @Autowired
    private LogoutService logoutService;

    @Test
    @DisplayName("logout: should validate and delete token for valid data")
    void logout_ValidData_ShouldValidateAndDelete() {
        Long userId = 1L;
        String jti = "unique-jti-12345";

        User mockUser = mock(User.class);
        RefreshToken mockRefreshToken = mock(RefreshToken.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(refreshTokenRepository.findById(jti)).thenReturn(Optional.of(mockRefreshToken));

        doNothing().when(logoutValidation).allValidation(mockUser, mockRefreshToken);

        logoutService.logout(userId, jti);

        verify(userRepository).findById(userId);
        verify(refreshTokenRepository).findById(jti);
        verify(logoutValidation).allValidation(mockUser, mockRefreshToken);
        verify(refreshTokenRepository).deleteByJti(jti);
        verifyNoMoreInteractions(userRepository, refreshTokenRepository, logoutValidation);
    }

    @Test
    @DisplayName("logout: should throw EntityNotFoundException if user not found")
    void logout_UserNotFound_ShouldThrow() {
        Long userId = 1L;
        String jti = "any-jti";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> logoutService.logout(userId, jti));

        verify(userRepository).findById(userId);
        verify(refreshTokenRepository, never()).findById(anyString());
        verify(logoutValidation, never()).allValidation(any(), any());
        verify(refreshTokenRepository, never()).deleteByJti(anyString());
    }

    @Test
    @DisplayName("logout: should throw EntityNotFoundException if refresh token not found")
    void logout_RefreshTokenNotFound_ShouldThrow() {
        Long userId = 1L;
        String jti = "invalid-jti";

        User mockUser = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(refreshTokenRepository.findById(jti)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> logoutService.logout(userId, jti));

        verify(userRepository).findById(userId);
        verify(refreshTokenRepository).findById(jti);
        verify(logoutValidation, never()).allValidation(any(), any());
        verify(refreshTokenRepository, never()).deleteByJti(anyString());
    }

    @Test
    @DisplayName("logout: should throw exception and NOT delete token if validation fails")
    void logout_InvalidData_ShouldThrowAndNotDelete() {
        Long userId = 1L;
        String jti = "invalid-jti";

        User mockUser = mock(User.class);
        RefreshToken mockRefreshToken = mock(RefreshToken.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(refreshTokenRepository.findById(jti)).thenReturn(Optional.of(mockRefreshToken));

        doThrow(new IllegalStateException("Invalid token"))
                .when(logoutValidation).allValidation(mockUser, mockRefreshToken);

        assertThrows(IllegalStateException.class, () -> logoutService.logout(userId, jti));

        verify(userRepository).findById(userId);
        verify(refreshTokenRepository).findById(jti);
        verify(logoutValidation).allValidation(mockUser, mockRefreshToken);
        verify(refreshTokenRepository, never()).deleteByJti(anyString());
    }
}