package com.culex.userService.service.auth.deleteAccount;

import com.culex.userService.DB.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteValidationTest {

    @Mock
    private User user;

    @InjectMocks
    private DeleteValidation deleteValidation;

    @Test
    @DisplayName("allValidation should not throw when user is not deleted")
    void allValidation_UserNotDeleted_ShouldPass() {
        when(user.isDeleted()).thenReturn(false);

        assertDoesNotThrow(() -> deleteValidation.allValidation(user));

        verify(user).isDeleted();
    }

    @Test
    @DisplayName("allValidation should throw IllegalStateException when user is deleted")
    void allValidation_UserDeleted_ShouldThrow() {
        when(user.isDeleted()).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> deleteValidation.allValidation(user));

        assertEquals("User already deleted", exception.getMessage());
        verify(user).isDeleted();
    }
}