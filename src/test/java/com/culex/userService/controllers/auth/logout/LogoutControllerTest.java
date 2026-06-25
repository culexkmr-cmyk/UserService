package com.culex.userService.controllers.auth.logout;

import com.culex.userService.service.auth.logout.LogoutService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("LogoutController Unit Tests")
public class LogoutControllerTest {

    @MockitoBean
    private LogoutService logoutService;

    @Autowired
    private LogoutController logoutController;

    @Test
    @DisplayName("logout: should return OK when logout succeeds")
    void logout_ValidRequest_ShouldReturnOk() {
        Long userId = 1L;
        String jti = "unique-jti-12345";

        doNothing().when(logoutService).logout(userId, jti);

        LogoutDto dto = new LogoutDto(jti);
        ResponseEntity<?> response = logoutController.logout(dto, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        verify(logoutService).logout(userId, jti);
    }

    @Test
    @DisplayName("logout: should propagate exception when service throws")
    void logout_ServiceThrows_ShouldPropagateException() {
        Long userId = 1L;
        String jti = "invalid-jti";

        doThrow(new RuntimeException("Token not found"))
                .when(logoutService).logout(userId, jti);

        LogoutDto dto = new LogoutDto(jti);

        assertThrows(RuntimeException.class, () -> logoutController.logout(dto, userId));
        verify(logoutService).logout(userId, jti);
    }
}