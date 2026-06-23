package com.culex.userService.service.auth.JWT;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TokenUpdateValidationTest {

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TokenUpdateValidation tokenUpdateValidation;

    @Test
    @DisplayName("userIdValidation: should return user if it exists in database")
    void userIdValidation_UserExists_ShouldReturnUser() {
        Long userId = 1L;
        User mockUser = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        User result = tokenUpdateValidation.userIdValidation(userId);

        assertSame(mockUser, result);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("userIdValidation: should throw EntityNotFoundException if user not found")
    void userIdValidation_UserNotFound_ShouldThrow() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> tokenUpdateValidation.userIdValidation(userId));

        assertEquals("User not found with id: 999", exception.getMessage());
    }


    @Test
    @DisplayName("jtiValidation: should return refresh token if it exists in database")
    void jtiValidation_TokenExists_ShouldReturnToken() {
        String jti = "valid-jti-123";
        RefreshToken mockToken = mock(RefreshToken.class);
        when(refreshTokenRepository.findByJti(jti)).thenReturn(Optional.of(mockToken));

        RefreshToken result = tokenUpdateValidation.jtiValidation(jti);

        assertSame(mockToken, result);
        verify(refreshTokenRepository).findByJti(jti);
    }

    @Test
    @DisplayName("jtiValidation: should throw EntityNotFoundException if token not found")
    void jtiValidation_TokenNotFound_ShouldThrow() {
        String jti = "invalid-jti";
        when(refreshTokenRepository.findByJti(jti)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> tokenUpdateValidation.jtiValidation(jti));

        assertEquals("Token not found with jti: invalid-jti", exception.getMessage());
    }


    @Test
    @DisplayName("allValidation: should return ValidationResponse with both token and user for valid data")
    void allValidation_ValidData_ShouldReturnResponse() {
        Long userId = 1L;
        String jti = "valid-jti";

        User mockUser = mock(User.class);
        RefreshToken mockToken = mock(RefreshToken.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(refreshTokenRepository.findByJti(jti)).thenReturn(Optional.of(mockToken));

        TokenUpdateValidation.ValidationResponse result = tokenUpdateValidation.allValidation(jti, userId);

        assertNotNull(result);
        assertSame(mockToken, result.refreshToken());
        assertSame(mockUser, result.user());

        verify(refreshTokenRepository).findByJti(jti);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("allValidation: should throw 'Token not found' if jti is invalid")
    void allValidation_InvalidJti_ShouldThrowTokenNotFound() {
        Long userId = 1L;
        String invalidJti = "invalid-jti";

        when(refreshTokenRepository.findByJti(invalidJti)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> tokenUpdateValidation.allValidation(invalidJti, userId));

        assertEquals("Token not found with jti: invalid-jti", exception.getMessage());

        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("allValidation: should throw 'User not found' if user is invalid")
    void allValidation_InvalidUser_ShouldThrowUserNotFound() {
        Long invalidUserId = 999L;
        String jti = "valid-jti";

        RefreshToken mockToken = mock(RefreshToken.class);
        when(refreshTokenRepository.findByJti(jti)).thenReturn(Optional.of(mockToken));
        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> tokenUpdateValidation.allValidation(jti, invalidUserId));

        assertEquals("User not found with id: 999", exception.getMessage());

        verify(refreshTokenRepository).findByJti(jti);
    }
}