package com.culex.userService.service.auth.register;

import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {
        "app.auth.register.password.length.max=30",
        "app.auth.register.password.length.min=6",
        "app.auth.register.username.length.max=20",
        "app.auth.register.username.length.min=4",
        "app.auth.register.nickname.length.max=25"
})
@DisplayName("RegValidation Unit Tests")
class RegValidationTest {

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private RegValidation regValidation;

    private final int minUsernameLength = 4;
    private final int maxUsernameLength = 20;
    private final int minPasswordLength = 6;
    private final int maxPasswordLength = 30;
    private final int maxNicknameLength = 25;

    private final String validUsername = "validUser";
    private final String validEmail = "valid@example.com";
    private final String validPassword = "ValidPass123";
    private final String validNickname = "ValidNick";

    @Test
    @DisplayName("allValidation should pass when all fields are valid")
    void allValidation_AllValid_ShouldPass() {
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> regValidation.allValidation(validNickname, validPassword, validUsername, validEmail));
    }

    @Test
    @DisplayName("allValidation should throw for null username")
    void allValidation_NullUsername_ShouldThrow() {
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> regValidation.allValidation(validNickname, validPassword, null, validEmail));
        assertTrue(exception.getMessage().contains("Username must be longer than"));
    }

    @Test
    @DisplayName("allValidation should throw for too short username")
    void allValidation_UsernameTooShort_ShouldThrow() {
        String shortUsername = "a".repeat(minUsernameLength - 1);
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> regValidation.allValidation(validNickname, validPassword, shortUsername, validEmail));
        assertTrue(exception.getMessage().contains("Username must be longer than"));
    }

    @Test
    @DisplayName("allValidation should throw for too long username")
    void allValidation_UsernameTooLong_ShouldThrow() {
        String longUsername = "a".repeat(maxUsernameLength + 1);
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> regValidation.allValidation(validNickname, validPassword, longUsername, validEmail));
        assertTrue(exception.getMessage().contains("less than"));
    }

    @Test
    @DisplayName("allValidation should throw if username already exists")
    void allValidation_UsernameAlreadyExists_ShouldThrow() {
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.of(mock(User.class)));
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> regValidation.allValidation(validNickname, validPassword, validUsername, validEmail));
        assertEquals("User with this username already exists", exception.getMessage());
    }

    @Test
    @DisplayName("allValidation should throw if username starts with 'deleted_'")
    void allValidation_UsernameStartsWithDeleted_ShouldThrow() {
        String deletedUsername = "deleted_user123";
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> regValidation.allValidation(validNickname, validPassword, deletedUsername, validEmail));
        assertEquals("Username cannot start with 'deleted_'", exception.getMessage());
    }

    @Test
    @DisplayName("allValidation should throw for null password")
    void allValidation_NullPassword_ShouldThrow() {
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> regValidation.allValidation(validNickname, null, validUsername, validEmail));
    }

    @Test
    @DisplayName("allValidation should throw for too short password")
    void allValidation_PasswordTooShort_ShouldThrow() {
        String shortPassword = "a".repeat(minPasswordLength - 1);
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> regValidation.allValidation(validNickname, shortPassword, validUsername, validEmail));
    }

    @Test
    @DisplayName("allValidation should throw for too long password")
    void allValidation_PasswordTooLong_ShouldThrow() {
        String longPassword = "a".repeat(maxPasswordLength + 1);
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> regValidation.allValidation(validNickname, longPassword, validUsername, validEmail));
    }

    @Test
    @DisplayName("allValidation should throw for null nickname")
    void allValidation_NullNickname_ShouldThrow() {
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> regValidation.allValidation(null, validPassword, validUsername, validEmail));
    }

    @Test
    @DisplayName("allValidation should throw for too long nickname")
    void allValidation_NicknameTooLong_ShouldThrow() {
        String longNickname = "a".repeat(maxNicknameLength + 1);
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> regValidation.allValidation(longNickname, validPassword, validUsername, validEmail));
        assertTrue(exception.getMessage().contains("Nickname must be less than"));
    }

    @Test
    @DisplayName("allValidation should throw for null email")
    void allValidation_NullEmail_ShouldThrow() {
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> regValidation.allValidation(validNickname, validPassword, validUsername, null));
    }

    @Test
    @DisplayName("allValidation should throw for invalid email format")
    void allValidation_InvalidEmailFormat_ShouldThrow() {
        String invalidEmail = "invalid-email-format";
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> regValidation.allValidation(validNickname, validPassword, validUsername, invalidEmail));
        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    @DisplayName("allValidation should throw if email starts with 'deleted_'")
    void allValidation_EmailStartsWithDeleted_ShouldThrow() {
        String deletedEmail = "deleted_email@example.com";
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> regValidation.allValidation(validNickname, validPassword, validUsername, deletedEmail));
        assertEquals("Email cannot start with 'deleted_'", exception.getMessage());
    }

    @Test
    @DisplayName("allValidation should throw if email already exists")
    void allValidation_EmailAlreadyExists_ShouldThrow() {
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.of(mock(User.class)));

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> regValidation.allValidation(validNickname, validPassword, validUsername, validEmail));
        assertEquals("Email already exists", exception.getMessage());
    }
}