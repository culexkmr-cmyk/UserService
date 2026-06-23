package com.culex.userService.service.auth.logout;

import com.culex.userService.DB.entities.RefreshToken;
import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.RefreshTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class LogoutValidationTest {

    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private LogoutValidation logoutValidation;

    @Test
    @DisplayName("jtiValidation: should return token if it exists in database")
    void jtiValidation_TokenExists_ShouldReturnToken() {
        String jti = "valid-jti";
        RefreshToken mockToken = mock(RefreshToken.class);
        when(refreshTokenRepository.findByJti(jti)).thenReturn(Optional.of(mockToken));

        RefreshToken result = logoutValidation.jtiValidation(jti);

        assertSame(mockToken, result);
    }

    @Test
    @DisplayName("jtiValidation: should throw BadCredentialsException if token not found")
    void jtiValidation_TokenNotFound_ShouldThrow() {
        String jti = "invalid-jti";
        when(refreshTokenRepository.findByJti(jti)).thenReturn(Optional.empty());

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> logoutValidation.jtiValidation(jti));

        assertEquals("Invalid token", exception.getMessage());
    }

    @Test
    @DisplayName("checkTokenOwner: should pass if user ID matches token owner")
    void checkTokenOwner_OwnerMatches_ShouldPass() {
        Long userId = 1L;

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);

        RefreshToken mockToken = mock(RefreshToken.class);
        when(mockToken.getUser()).thenReturn(mockUser);

        assertDoesNotThrow(() -> logoutValidation.checkTokenOwner(userId, mockToken));
    }

    @Test
    @DisplayName("checkTokenOwner: should throw BadCredentialsException if user ID does not match")
    void checkTokenOwner_OwnerMismatch_ShouldThrow() {
        Long userId = 1L;
        Long anotherUserId = 2L;

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(anotherUserId);

        RefreshToken mockToken = mock(RefreshToken.class);
        when(mockToken.getUser()).thenReturn(mockUser);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> logoutValidation.checkTokenOwner(userId, mockToken));

        assertEquals("Token ownership verification failed", exception.getMessage());
    }

    @Test
    @DisplayName("allValidation: should pass for valid jti and correct owner")
    void allValidation_ValidData_ShouldPass() {
        Long userId = 1L;
        String jti = "valid-jti";

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);

        RefreshToken mockToken = mock(RefreshToken.class);
        when(mockToken.getUser()).thenReturn(mockUser);

        when(refreshTokenRepository.findByJti(jti)).thenReturn(Optional.of(mockToken));

        assertDoesNotThrow(() -> logoutValidation.allValidation(userId, jti));

        verify(refreshTokenRepository).findByJti(jti);
    }

    @Test
    @DisplayName("allValidation: should throw 'Invalid token' if jti is not found")
    void allValidation_InvalidJti_ShouldThrowInvalidToken() {
        Long userId = 1L;
        String jti = "invalid-jti";
        when(refreshTokenRepository.findByJti(jti)).thenReturn(Optional.empty());

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> logoutValidation.allValidation(userId, jti));

        assertEquals("Invalid token", exception.getMessage());
    }

    @Test
    @DisplayName("allValidation: should throw 'Token ownership verification failed' if owner mismatch")
    void allValidation_InvalidOwner_ShouldThrowOwnershipFailed() {
        Long userId = 1L;
        Long anotherUserId = 2L;
        String jti = "valid-jti";

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(anotherUserId);

        RefreshToken mockToken = mock(RefreshToken.class);
        when(mockToken.getUser()).thenReturn(mockUser);

        when(refreshTokenRepository.findByJti(jti)).thenReturn(Optional.of(mockToken));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> logoutValidation.allValidation(userId, jti));

        assertEquals("Token ownership verification failed", exception.getMessage());
    }
}