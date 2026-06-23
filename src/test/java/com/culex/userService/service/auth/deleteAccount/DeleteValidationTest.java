package com.culex.userService.service.auth.deleteAccount;

import com.culex.userService.DB.entities.User;
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
public class DeleteValidationTest {

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private DeleteValidation deleteValidation;

    @Test
    @DisplayName("userIdValidation: should return user if it exists in database")
    void userIdValidation_UserExists_ShouldReturnUser() {
        Long userId = 1L;
        User mockUser = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        User result = deleteValidation.userIdValidation(userId);

        assertSame(mockUser, result);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("userIdValidation: should throw EntityNotFoundException if user not found")
    void userIdValidation_UserNotFound_ShouldThrow() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> deleteValidation.userIdValidation(userId));

        assertEquals("User not found with id: 999", exception.getMessage());
    }
    @Test
    @DisplayName("isUserDelete: should return user if not deleted")
    void isUserDelete_NotDeleted_ShouldReturnUser() {
        User mockUser = mock(User.class);
        when(mockUser.isDeleted()).thenReturn(false);

        User result = deleteValidation.isUserDelete(mockUser);

        assertSame(mockUser, result);
        verify(mockUser).isDeleted();
    }

    @Test
    @DisplayName("isUserDelete: should throw IllegalStateException if user already deleted")
    void isUserDelete_AlreadyDeleted_ShouldThrow() {
        User mockUser = mock(User.class);
        when(mockUser.isDeleted()).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> deleteValidation.isUserDelete(mockUser));

        assertEquals("User already deleted", exception.getMessage());
    }

    @Test
    @DisplayName("allValidation: should return user if exists and not deleted")
    void allValidation_ValidUser_ShouldReturnUser() {
        Long userId = 1L;
        User mockUser = mock(User.class);
        when(mockUser.isDeleted()).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        User result = deleteValidation.allValidation(userId);

        assertSame(mockUser, result);
        verify(userRepository).findById(userId);
        verify(mockUser).isDeleted();
    }

    @Test
    @DisplayName("allValidation: should throw 'User not found' if user does not exist")
    void allValidation_UserNotFound_ShouldThrowUserNotFound() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> deleteValidation.allValidation(userId));

        assertEquals("User not found with id: 999", exception.getMessage());
        
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("allValidation: should throw 'User already deleted' if user is already deleted")
    void allValidation_AlreadyDeleted_ShouldThrowAlreadyDeleted() {
        Long userId = 1L;
        User mockUser = mock(User.class);
        when(mockUser.isDeleted()).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> deleteValidation.allValidation(userId));

        assertEquals("User already deleted", exception.getMessage());
        
        verify(userRepository).findById(userId);
        verify(mockUser).isDeleted();
    }
}