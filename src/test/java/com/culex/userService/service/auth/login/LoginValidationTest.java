package com.culex.userService.service.auth.login;

import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class LoginValidationTest {

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder encoder;

    @Autowired
    private LoginValidation loginValidation;

    @Test
    @DisplayName("usernameValidation: should return user if username exists")
    void usernameValidation_UserExists_ShouldReturnUser() {
        String username = "validUser";
        User mockUser = mock(User.class);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        User result = loginValidation.usernameValidation(username);

        assertSame(mockUser, result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    @DisplayName("usernameValidation: should throw UsernameNotFoundException if user not found")
    void usernameValidation_UserNotFound_ShouldThrow() {
        String username = "invalidUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> loginValidation.usernameValidation(username));

        assertEquals("User not found with username: invalidUser", exception.getMessage());
    }

    @Test
    @DisplayName("passwordValidation: should return user if password matches")
    void passwordValidation_CorrectPassword_ShouldReturnUser() {
        String password = "correctPassword123";
        String hashedPassword = "hashedPassword123";

        User mockUser = mock(User.class);
        when(mockUser.getPassword()).thenReturn(hashedPassword);
        when(mockUser.getUsername()).thenReturn("testUser");

        when(encoder.matches(password, hashedPassword)).thenReturn(true);

        User result = loginValidation.passwordValidation(password, mockUser);

        assertSame(mockUser, result);
        verify(encoder).matches(password, hashedPassword);
    }

    @Test
    @DisplayName("passwordValidation: should throw BadCredentialsException if password does not match")
    void passwordValidation_WrongPassword_ShouldThrow() {
        String password = "wrongPassword";
        String hashedPassword = "hashedPassword123";

        User mockUser = mock(User.class);
        when(mockUser.getPassword()).thenReturn(hashedPassword);
        when(mockUser.getUsername()).thenReturn("testUser");

        when(encoder.matches(password, hashedPassword)).thenReturn(false);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> loginValidation.passwordValidation(password, mockUser));

        assertEquals("Invalid password for user: testUser", exception.getMessage());
    }

    @Test
    @DisplayName("isUserDelete: should return user if not deleted")
    void isUserDelete_NotDeleted_ShouldReturnUser() {
        User mockUser = mock(User.class);
        when(mockUser.isDeleted()).thenReturn(false);

        User result = loginValidation.isUserDelete(mockUser);

        assertSame(mockUser, result);
        verify(mockUser).isDeleted();
    }

    @Test
    @DisplayName("isUserDelete: should throw IllegalStateException if user is deleted")
    void isUserDelete_Deleted_ShouldThrow() {
        User mockUser = mock(User.class);
        when(mockUser.isDeleted()).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> loginValidation.isUserDelete(mockUser));

        assertEquals("User is deleted", exception.getMessage());
    }

    @Test
    @DisplayName("allValidation: should return user if all validations pass")
    void allValidation_AllValid_ShouldReturnUser() {
        String password = "correctPassword123";
        String username = "validUser";
        String hashedPassword = "hashedPassword123";

        User mockUser = mock(User.class);
        when(mockUser.getPassword()).thenReturn(hashedPassword);
        when(mockUser.getUsername()).thenReturn(username);
        when(mockUser.isDeleted()).thenReturn(false);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(encoder.matches(password, hashedPassword)).thenReturn(true);

        User result = loginValidation.allValidation(password, username);

        assertSame(mockUser, result);
        verify(userRepository).findByUsername(username);
        verify(mockUser).isDeleted();
        verify(encoder).matches(password, hashedPassword);
    }

    @Test
    @DisplayName("allValidation: should throw 'User not found' if username is invalid")
    void allValidation_InvalidUsername_ShouldThrowUserNotFound() {
        String password = "anyPassword";
        String invalidUsername = "invalidUser";

        when(userRepository.findByUsername(invalidUsername)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> loginValidation.allValidation(password, invalidUsername));

        assertEquals("User not found with username: invalidUser", exception.getMessage());

        verify(encoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("allValidation: should throw 'User is deleted' if user is deleted")
    void allValidation_DeletedUser_ShouldThrowUserDeleted() {
        String password = "anyPassword";
        String username = "deletedUser";

        User mockUser = mock(User.class);
        when(mockUser.isDeleted()).thenReturn(true);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> loginValidation.allValidation(password, username));

        assertEquals("User is deleted", exception.getMessage());

        verify(encoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("allValidation: should throw 'Invalid password' if password is wrong")
    void allValidation_WrongPassword_ShouldThrowInvalidPassword() {
        String password = "wrongPassword";
        String username = "validUser";
        String hashedPassword = "hashedPassword123";

        User mockUser = mock(User.class);
        when(mockUser.getPassword()).thenReturn(hashedPassword);
        when(mockUser.getUsername()).thenReturn(username);
        when(mockUser.isDeleted()).thenReturn(false);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(encoder.matches(password, hashedPassword)).thenReturn(false);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> loginValidation.allValidation(password, username));

        assertEquals("Invalid password for user: validUser", exception.getMessage());
    }
}