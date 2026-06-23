package com.culex.userService.service.user.changeNickname;

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

@SpringBootTest(properties = {
        "app.auth.register.nickname.length.max=25"
})
@DisplayName("ChangeNicknameValidation Unit Tests")
class ChangeNicknameValidationTest {

    @MockitoBean
    private UserRepository repository;

    @Autowired
    private ChangeNicknameValidation validation;

    private final int maxNicknameLength = 25;

    @Test
    @DisplayName("nicknameValidation: should pass for valid nickname")
    void nicknameValidation_ValidNickname_ShouldPass() {
        String validNickname = "ValidNickname123";
        assertDoesNotThrow(() -> validation.nicknameValidation(validNickname));
    }

    @Test
    @DisplayName("nicknameValidation: should throw exception for null nickname")
    void nicknameValidation_NullNickname_ShouldThrow() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> validation.nicknameValidation(null));
        assertTrue(exception.getMessage().contains("Nickname must be less than"));
    }

    @Test
    @DisplayName("nicknameValidation: should throw exception if nickname is too long")
    void nicknameValidation_TooLong_ShouldThrow() {
        String longNickname = "a".repeat(maxNicknameLength + 1);
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> validation.nicknameValidation(longNickname));
        assertTrue(exception.getMessage().contains("Nickname must be less than"));
    }

    @Test
    @DisplayName("userIdValidation: should return user if user exists")
    void userIdValidation_UserExists_ShouldReturnUser() {
        Long userId = 1L;
        User mockUser = mock(User.class);
        when(repository.findById(userId)).thenReturn(Optional.of(mockUser));

        User result = validation.userIdValidation(userId);

        assertSame(mockUser, result);
        verify(repository).findById(userId);
    }

    @Test
    @DisplayName("userIdValidation: should throw EntityNotFoundException if user not found")
    void userIdValidation_UserNotFound_ShouldThrow() {
        Long userId = 999L;
        when(repository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> validation.userIdValidation(userId));

        assertEquals("User not found with id: 999", exception.getMessage());
    }

    @Test
    @DisplayName("isUserDelete: should return user if not deleted")
    void isUserDelete_NotDeleted_ShouldReturnUser() {
        User mockUser = mock(User.class);
        when(mockUser.isDeleted()).thenReturn(false);

        User result = validation.isUserDelete(mockUser);

        assertSame(mockUser, result);
        verify(mockUser).isDeleted();
    }

    @Test
    @DisplayName("isUserDelete: should throw IllegalStateException if user is deleted")
    void isUserDelete_Deleted_ShouldThrow() {
        User mockUser = mock(User.class);
        when(mockUser.isDeleted()).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> validation.isUserDelete(mockUser));

        assertEquals("User is deleted", exception.getMessage());
    }

    @Test
    @DisplayName("allValidation: should return user if all validations pass")
    void allValidation_AllValid_ShouldReturnUser() {
        String newNickname = "NewNickname";
        Long userId = 1L;

        User mockUser = mock(User.class);
        when(mockUser.isDeleted()).thenReturn(false);
        when(repository.findById(userId)).thenReturn(Optional.of(mockUser));

        User result = validation.allValidation(newNickname, userId);

        assertSame(mockUser, result);
        verify(repository).findById(userId);
        verify(mockUser).isDeleted();
    }

    @Test
    @DisplayName("allValidation: should throw exception if nickname is too long")
    void allValidation_TooLongNickname_ShouldThrow() {
        String longNickname = "a".repeat(maxNicknameLength + 1);
        Long userId = 1L;

        assertThrows(IllegalArgumentException.class,
                () -> validation.allValidation(longNickname, userId));

        verify(repository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("allValidation: should throw 'User not found' if user does not exist")
    void allValidation_UserNotFound_ShouldThrow() {
        String newNickname = "ValidNickname";
        Long userId = 999L;

        when(repository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> validation.allValidation(newNickname, userId));

        assertEquals("User not found with id: 999", exception.getMessage());
    }

    @Test
    @DisplayName("allValidation: should throw 'User is deleted' if user is deleted")
    void allValidation_DeletedUser_ShouldThrow() {
        String newNickname = "ValidNickname";
        Long userId = 1L;

        User mockUser = mock(User.class);
        when(mockUser.isDeleted()).thenReturn(true);
        when(repository.findById(userId)).thenReturn(Optional.of(mockUser));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> validation.allValidation(newNickname, userId));

        assertEquals("User is deleted", exception.getMessage());
    }
}