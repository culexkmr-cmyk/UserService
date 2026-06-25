package com.culex.userService.service.auth.login;

import com.culex.userService.DB.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class LoginValidationTest {

    @MockitoBean
    private PasswordEncoder encoder;

    @Autowired
    private LoginValidation loginValidation;

    @Test
    @DisplayName("allValidation should not throw when password matches and user not deleted")
    void allValidation_Valid_ShouldPass() {
        String password = "correctPassword";
        String hashedPassword = "hashedCorrect";
        User mockUser = mock(User.class);
        when(mockUser.getPassword()).thenReturn(hashedPassword);
        when(mockUser.getUsername()).thenReturn("testUser");
        when(mockUser.isDeleted()).thenReturn(false);
        when(encoder.matches(password, hashedPassword)).thenReturn(true);

        assertDoesNotThrow(() -> loginValidation.allValidation(password, mockUser));

        verify(encoder).matches(password, hashedPassword);
        verify(mockUser).isDeleted();
    }

    @Test
    @DisplayName("allValidation should throw BadCredentialsException when password does not match")
    void allValidation_WrongPassword_ShouldThrow() {
        String password = "wrongPassword";
        String hashedPassword = "hashedCorrect";
        User mockUser = mock(User.class);
        when(mockUser.getPassword()).thenReturn(hashedPassword);
        when(mockUser.getUsername()).thenReturn("testUser");
        when(mockUser.isDeleted()).thenReturn(false);
        when(encoder.matches(password, hashedPassword)).thenReturn(false);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> loginValidation.allValidation(password, mockUser));

        assertEquals("Invalid password for user: testUser", exception.getMessage());
        verify(encoder).matches(password, hashedPassword);
        verify(mockUser, never()).isDeleted();
    }

    @Test
    @DisplayName("allValidation should throw IllegalStateException when user is deleted")
    void allValidation_DeletedUser_ShouldThrow() {
        String password = "correctPassword";
        String hashedPassword = "hashedCorrect";
        User mockUser = mock(User.class);
        when(mockUser.getPassword()).thenReturn(hashedPassword);
        when(mockUser.getUsername()).thenReturn("testUser");
        when(mockUser.isDeleted()).thenReturn(true);
        when(encoder.matches(password, hashedPassword)).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> loginValidation.allValidation(password, mockUser));

        assertEquals("User is deleted", exception.getMessage());
        verify(encoder).matches(password, hashedPassword);
        verify(mockUser).isDeleted();
    }
}