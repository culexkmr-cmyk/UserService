package com.culex.userService.service.user.changePassword;

import com.culex.userService.DB.entities.PasswordResetToken;
import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.PasswordResetTokenRepository;
import com.culex.userService.DB.repositories.UserRepository;
import com.culex.userService.client.NotificationDispatcher;
import com.culex.userService.client.NotificationType;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("ChangePasswordService Unit Tests")
public class ChangePasswordServiceTest {

    @MockitoBean
    private ChangePasswordValidation validation;

    @MockitoBean
    private PasswordResetTokenRepository resetTokenRepository;

    @MockitoBean
    private NotificationDispatcher notificationDispatcher;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder encoder;

    @Autowired
    private ChangePasswordService changePasswordService;

    @Test
    @DisplayName("savePasswordResetToken: should generate token, save it, send notification and return token")
    void savePasswordResetToken_ValidUser_ShouldGenerateAndSave() {
        Long userId = 1L;
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);
        when(mockUser.getEmail()).thenReturn("test@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(encoder.encode(anyString())).thenReturn("hashedCode");
        
        PasswordResetToken mockToken = mock(PasswordResetToken.class);
        when(resetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(mockToken);

        PasswordResetToken result = changePasswordService.savePasswordResetToken(userId);

        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(resetTokenRepository).deleteAllByUser(mockUser);
        verify(encoder).encode(anyString());
        verify(resetTokenRepository).save(any(PasswordResetToken.class));
        verify(notificationDispatcher).sendNotification(eq(NotificationType.EMAIL), eq("test@example.com"), anyString());
    }

    @Test
    @DisplayName("savePasswordResetToken: should throw EntityNotFoundException if user not found")
    void savePasswordResetToken_UserNotFound_ShouldThrow() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> changePasswordService.savePasswordResetToken(userId));

        verify(userRepository).findById(userId);
        verify(resetTokenRepository, never()).deleteAllByUser(any());
        verify(resetTokenRepository, never()).save(any());
        verify(notificationDispatcher, never()).sendNotification(any(), anyString(), anyString());
    }

    @Test
    @DisplayName("setNewPassword: should validate, set new password and save user for valid data")
    void setNewPassword_ValidData_ShouldSetAndSave() {
        String rawPassword = "NewPassword123";
        Long userId = 1L;
        String tokenString = "validToken";

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);

        PasswordResetToken mockToken = mock(PasswordResetToken.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(resetTokenRepository.findById(tokenString)).thenReturn(Optional.of(mockToken));
        doNothing().when(validation).allValidation(rawPassword, mockUser, mockToken);
        when(encoder.encode(rawPassword)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User result = changePasswordService.setNewPassword(rawPassword, userId, tokenString);

        assertSame(mockUser, result);

        verify(userRepository).findById(userId);
        verify(resetTokenRepository).findById(tokenString);
        verify(validation).allValidation(rawPassword, mockUser, mockToken);
        verify(mockUser).setPassword("encodedPassword");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertSame(mockUser, capturedUser);
    }

    @Test
    @DisplayName("setNewPassword: should throw EntityNotFoundException if user not found")
    void setNewPassword_UserNotFound_ShouldThrow() {
        String rawPassword = "NewPassword123";
        Long userId = 999L;
        String tokenString = "validToken";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> changePasswordService.setNewPassword(rawPassword, userId, tokenString));

        verify(userRepository).findById(userId);
        verify(resetTokenRepository, never()).findById(anyString());
        verify(validation, never()).allValidation(anyString(), any(), any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("setNewPassword: should throw EntityNotFoundException if token not found")
    void setNewPassword_TokenNotFound_ShouldThrow() {
        String rawPassword = "NewPassword123";
        Long userId = 1L;
        String tokenString = "invalidToken";

        User mockUser = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(resetTokenRepository.findById(tokenString)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> changePasswordService.setNewPassword(rawPassword, userId, tokenString));

        verify(userRepository).findById(userId);
        verify(resetTokenRepository).findById(tokenString);
        verify(validation, never()).allValidation(anyString(), any(), any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("setNewPassword: should throw exception and NOT save user if validation fails")
    void setNewPassword_InvalidData_ShouldThrowAndNotSave() {
        String rawPassword = "short";
        Long userId = 1L;
        String tokenString = "validToken";

        User mockUser = mock(User.class);
        PasswordResetToken mockToken = mock(PasswordResetToken.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(resetTokenRepository.findById(tokenString)).thenReturn(Optional.of(mockToken));

        doThrow(new IllegalArgumentException("Password must be longer than 7 and less than 19 character"))
                .when(validation).allValidation(rawPassword, mockUser, mockToken);

        assertThrows(IllegalArgumentException.class, () -> changePasswordService.setNewPassword(rawPassword, userId, tokenString));

        verify(userRepository).findById(userId);
        verify(resetTokenRepository).findById(tokenString);
        verify(validation).allValidation(rawPassword, mockUser, mockToken);
        verify(userRepository, never()).save(any(User.class));
    }
}