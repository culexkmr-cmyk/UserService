package com.culex.userService.service.user.changePassword;

import com.culex.userService.DB.entities.PasswordResetToken;
import com.culex.userService.DB.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {
        "app.auth.register.password.length.max=20",
        "app.auth.register.password.length.min=8"
})
@DisplayName("ChangePasswordValidation Unit Tests")
class ChangePasswordValidationTest {

    @Autowired
    private ChangePasswordValidation validation;

    private final int minPasswordLength = 8;
    private final int maxPasswordLength = 20;

    @Test
    @DisplayName("allValidation should pass for valid password and correct token owner")
    void allValidation_Valid_ShouldPass() {
        String validPassword = "ValidPassword123";
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1L);

        PasswordResetToken mockToken = mock(PasswordResetToken.class);
        User tokenUser = mock(User.class);
        when(tokenUser.getId()).thenReturn(1L);
        when(mockToken.getUser()).thenReturn(tokenUser);

        assertDoesNotThrow(() -> validation.allValidation(validPassword, mockUser, mockToken));
    }

    @Test
    @DisplayName("allValidation should throw IllegalArgumentException for null password")
    void allValidation_NullPassword_ShouldThrow() {
        User mockUser = mock(User.class);
        PasswordResetToken mockToken = mock(PasswordResetToken.class);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> validation.allValidation(null, mockUser, mockToken));
        assertTrue(exception.getMessage().contains("Password must be longer than"));
    }

    @Test
    @DisplayName("allValidation should throw IllegalArgumentException for too short password")
    void allValidation_TooShortPassword_ShouldThrow() {
        String shortPassword = "a".repeat(minPasswordLength - 1);
        User mockUser = mock(User.class);
        PasswordResetToken mockToken = mock(PasswordResetToken.class);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> validation.allValidation(shortPassword, mockUser, mockToken));
        assertTrue(exception.getMessage().contains("Password must be longer than"));
    }

    @Test
    @DisplayName("allValidation should throw IllegalArgumentException for too long password")
    void allValidation_TooLongPassword_ShouldThrow() {
        String longPassword = "a".repeat(maxPasswordLength + 1);
        User mockUser = mock(User.class);
        PasswordResetToken mockToken = mock(PasswordResetToken.class);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> validation.allValidation(longPassword, mockUser, mockToken));
        assertTrue(exception.getMessage().contains("Password must be longer than"));
    }

    @Test
    @DisplayName("allValidation should throw BadCredentialsException for wrong token owner")
    void allValidation_WrongTokenOwner_ShouldThrow() {
        String validPassword = "ValidPassword123";
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1L);

        PasswordResetToken mockToken = mock(PasswordResetToken.class);
        User tokenUser = mock(User.class);
        when(tokenUser.getId()).thenReturn(2L); // Другой ID пользователя
        when(mockToken.getUser()).thenReturn(tokenUser);

        Exception exception = assertThrows(BadCredentialsException.class,
                () -> validation.allValidation(validPassword, mockUser, mockToken));
        assertEquals("Token ownership verification failed", exception.getMessage());
    }
}