package com.culex.userService.service.user.changeNickname;

import com.culex.userService.DB.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {
        "app.auth.register.nickname.length.max=25"
})
@DisplayName("ChangeNicknameValidation Unit Tests")
class ChangeNicknameValidationTest {

    @Autowired
    private ChangeNicknameValidation validation;

    private final int maxNicknameLength = 25;

    @Test
    @DisplayName("allValidation should pass for valid nickname and non-deleted user")
    void allValidation_Valid_ShouldPass() {
        String validNickname = "ValidNickname123";
        User mockUser = mock(User.class);
        when(mockUser.isDeleted()).thenReturn(false);

        assertDoesNotThrow(() -> validation.allValidation(validNickname, mockUser));
        verify(mockUser).isDeleted();
    }

    @Test
    @DisplayName("allValidation should throw IllegalArgumentException for null nickname")
    void allValidation_NullNickname_ShouldThrow() {
        User mockUser = mock(User.class);
        when(mockUser.isDeleted()).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> validation.allValidation(null, mockUser));
        assertTrue(exception.getMessage().contains("Nickname must be less than"));
        verify(mockUser, never()).isDeleted(); // проверка удалённости не выполняется
    }

    @Test
    @DisplayName("allValidation should throw IllegalArgumentException for too long nickname")
    void allValidation_TooLongNickname_ShouldThrow() {
        String longNickname = "a".repeat(maxNicknameLength + 1);
        User mockUser = mock(User.class);
        when(mockUser.isDeleted()).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> validation.allValidation(longNickname, mockUser));
        assertTrue(exception.getMessage().contains("Nickname must be less than"));
        verify(mockUser, never()).isDeleted();
    }

    @Test
    @DisplayName("allValidation should throw IllegalStateException for deleted user")
    void allValidation_DeletedUser_ShouldThrow() {
        String validNickname = "ValidNickname";
        User mockUser = mock(User.class);
        when(mockUser.isDeleted()).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> validation.allValidation(validNickname, mockUser));
        assertEquals("User is deleted", exception.getMessage());
        verify(mockUser).isDeleted();
    }
}