package com.culex.userService.service.auth.logout;

import com.culex.userService.DB.entities.RefreshToken;
import com.culex.userService.DB.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class LogoutValidationTest {

    @Autowired
    private LogoutValidation logoutValidation;

    @Test
    @DisplayName("allValidation should not throw when user owns the token")
    void allValidation_OwnerMatches_ShouldPass() {
        Long userId = 1L;
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);

        User tokenUser = mock(User.class);
        when(tokenUser.getId()).thenReturn(userId);

        RefreshToken mockToken = mock(RefreshToken.class);
        when(mockToken.getUser()).thenReturn(tokenUser);

        assertDoesNotThrow(() -> logoutValidation.allValidation(mockUser, mockToken));
    }

    @Test
    @DisplayName("allValidation should throw BadCredentialsException when user does not own the token")
    void allValidation_OwnerMismatch_ShouldThrow() {
        Long userId = 1L;
        Long anotherUserId = 2L;

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);

        User tokenUser = mock(User.class);
        when(tokenUser.getId()).thenReturn(anotherUserId);

        RefreshToken mockToken = mock(RefreshToken.class);
        when(mockToken.getUser()).thenReturn(tokenUser);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> logoutValidation.allValidation(mockUser, mockToken));

        assertEquals("Token ownership verification failed", exception.getMessage());
    }
}