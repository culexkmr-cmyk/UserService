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

    // Test configuration values
    private final int minUsernameLength = 4;
    private final int maxUsernameLength = 20;
    private final int minPasswordLength = 6;
    private final int maxPasswordLength = 30;
    private final int maxNicknameLength = 25;

    @Test
    @DisplayName("usernameValidation: should pass for valid username")
    void usernameValidation_ValidUsername_ShouldPass() {
        String validUsername = "validUser123";
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> regValidation.usernameValidation(validUsername));
    }

    @Test
    @DisplayName("usernameValidation: should throw exception for null username")
    void usernameValidation_NullUsername_ShouldThrow() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> regValidation.usernameValidation(null));
        assertTrue(exception.getMessage().contains("Username must be longer than"));
    }

    @Test
    @DisplayName("usernameValidation: should throw exception if username is too short")
    void usernameValidation_TooShort_ShouldThrow() {
        String shortUsername = "a".repeat(minUsernameLength - 1);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> regValidation.usernameValidation(shortUsername));
        assertTrue(exception.getMessage().contains("Username must be longer than"));
    }

    @Test
    @DisplayName("usernameValidation: should throw exception if username is too long")
    void usernameValidation_TooLong_ShouldThrow() {
        String longUsername = "a".repeat(maxUsernameLength + 1);
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> regValidation.usernameValidation(longUsername));
        assertTrue(exception.getMessage().contains("less than"));
    }

    @Test
    @DisplayName("usernameValidation: should throw exception if username already exists")
    void usernameValidation_AlreadyExists_ShouldThrow() {
        String existingUsername = "existingUser";

        when(userRepository.findByUsername(existingUsername)).thenReturn(Optional.of(mock(User.class)));

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> regValidation.usernameValidation(existingUsername));
        assertEquals("User with this username already exists", exception.getMessage());
    }

    @Test
    @DisplayName("usernameValidation: should throw exception if username starts with 'deleted_'")
    void usernameValidation_StartsWithDeleted_ShouldThrow() {
        String deletedUsername = "deleted_user123";

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> regValidation.usernameValidation(deletedUsername));
        assertEquals("Username cannot start with 'deleted_'", exception.getMessage());
    }


    @Test
    @DisplayName("passwordValidation: should pass for valid password")
    void passwordValidation_ValidPassword_ShouldPass() {
        String validPassword = "SecurePass123!";
        assertDoesNotThrow(() -> regValidation.passwordValidation(validPassword));
    }

    @Test
    @DisplayName("passwordValidation: should throw exception for null password")
    void passwordValidation_NullPassword_ShouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> regValidation.passwordValidation(null));
    }


    @Test
    @DisplayName("passwordValidation: should throw exception if password is too short")
    void passwordValidation_TooShort_ShouldThrow() {
        String shortPassword = "a".repeat(minPasswordLength - 1);

        assertThrows(IllegalArgumentException.class,
                () -> regValidation.passwordValidation(shortPassword));
    }

    @Test
    @DisplayName("passwordValidation: should throw exception if password is too long")
    void passwordValidation_TooLong_ShouldThrow() {
        String longPassword = "a".repeat(maxPasswordLength + 1);
        assertThrows(IllegalArgumentException.class,
                () -> regValidation.passwordValidation(longPassword));
    }


    @Test
    @DisplayName("nicknameValidation: should pass for valid nickname")
    void nicknameValidation_ValidNickname_ShouldPass() {
        String validNickname = "CoolNickname";
        assertDoesNotThrow(() -> regValidation.nicknameValidation(validNickname));
    }

    @Test
    @DisplayName("nicknameValidation: should throw exception for null nickname")
    void nicknameValidation_NullNickname_ShouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> regValidation.nicknameValidation(null));
    }

    @Test
    @DisplayName("nicknameValidation: should throw exception if nickname is too long")
    void nicknameValidation_TooLong_ShouldThrow() {
        String longNickname = "a".repeat(maxNicknameLength + 1);
        assertThrows(IllegalArgumentException.class,
                () -> regValidation.nicknameValidation(longNickname));
    }


    @Test
    @DisplayName("emailValidation: should pass for valid email")
    void emailValidation_ValidEmail_ShouldPass() {
        String validEmail = "user@example.com";
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> regValidation.emailValidation(validEmail));
    }

    @Test
    @DisplayName("emailValidation: should throw exception for null email")
    void emailValidation_NullEmail_ShouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> regValidation.emailValidation(null));
    }

    @Test
    @DisplayName("emailValidation: should throw exception for invalid email format")
    void emailValidation_InvalidFormat_ShouldThrow() {
        String invalidEmail = "invalid-email-format";
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> regValidation.emailValidation(invalidEmail));
        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    @DisplayName("emailValidation: should throw exception if email starts with 'deleted_'")
    void emailValidation_StartsWithDeleted_ShouldThrow() {
        String deletedEmail = "deleted_email@example.com";
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> regValidation.emailValidation(deletedEmail));
        assertEquals("Email cannot start with 'deleted_'", exception.getMessage());
    }

    @Test
    @DisplayName("emailValidation: should throw exception if email already exists")
    void emailValidation_AlreadyExists_ShouldThrow() {
        String existingEmail = "existing@example.com";
        // Using mock(User.class) to bypass the protected constructor
        when(userRepository.findByEmail(existingEmail)).thenReturn(Optional.of(mock(User.class)));

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> regValidation.emailValidation(existingEmail));
        assertEquals("Email already exists", exception.getMessage());
    }


    @Test
    @DisplayName("allValidation: should pass when all fields are valid")
    void allValidation_AllValid_ShouldPass() {
        String username = "validUser";
        String email = "valid@example.com";
        String password = "ValidPass123";
        String nickname = "ValidNick";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> regValidation.allValidation(nickname, password, username, email));
    }

    @Test
    @DisplayName("allValidation: should throw exception if any single field is invalid")
    void allValidation_OneInvalidField_ShouldThrow() {
        String validUsername = "validUser";
        String invalidEmail = "invalid-email";
        String validPassword = "ValidPass123";
        String validNickname = "ValidNick";

        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> regValidation.allValidation(validNickname, validPassword, validUsername, invalidEmail));
    }
}