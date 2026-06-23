package com.culex.userService.service.auth.logout;

import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.RefreshTokenRepository;
import com.culex.userService.DB.repositories.UserRepository;
import com.culex.userService.service.auth.register.RegService;
import com.culex.userService.service.auth.register.RegValidation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
public class LogoutServiceTest {

    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;
    @MockitoBean
    private LogoutValidation logoutValidation;

    @Autowired
    private LogoutService logoutService;

    @Test
    @DisplayName("logout: should validate and delete token by jti for valid data")
    void logout_ValidData_ShouldValidateAndDelete() {
        Long userId = 1L;
        String jti = "unique-jti-12345";

        assertDoesNotThrow(() -> logoutService.logout(userId, jti));

        verify(logoutValidation).allValidation(userId, jti);

        verify(refreshTokenRepository).deleteByJti(jti);

        verifyNoMoreInteractions(refreshTokenRepository, logoutValidation);
    }

    @Test
    @DisplayName("logout: should throw exception and NOT delete token if validation fails")
    void logout_InvalidData_ShouldThrowAndNotDelete() {
        Long userId = 1L;
        String jti = "invalid-jti";

        doThrow(new IllegalArgumentException("Invalid token"))
                .when(logoutValidation).allValidation(userId, jti);

        assertThrows(IllegalArgumentException.class, () -> {
            logoutService.logout(userId, jti);
        });

        verify(logoutValidation).allValidation(userId, jti);
        verify(refreshTokenRepository, never()).deleteByJti(anyString());
    }
}