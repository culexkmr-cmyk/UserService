package com.culex.userService.controllers.auth.login;

import com.culex.userService.service.auth.login.LoginResponse;
import com.culex.userService.service.auth.login.LoginService;
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
@DisplayName("LoginController Unit Tests")
public class LoginControllerTest {

    @MockitoBean
    private LoginService loginService;

    @Autowired
    private LoginController loginController;

    @Test
    @DisplayName("login: should return OK with tokens when service returns LoginResponse")
    void login_ValidRequest_ShouldReturnOkWithTokens() {
        String password = "SecurePass123!";
        String username = "validUser";
        LoginResponse mockResponse = new LoginResponse("access-token", "refresh-token");

        when(loginService.login(password, username)).thenReturn(mockResponse);

        LoginDto dto = new LoginDto(password, username);
        ResponseEntity<?> response = loginController.login(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockResponse, response.getBody());
        verify(loginService).login(password, username);
    }

    @Test
    @DisplayName("login: should propagate exception when service throws")
    void login_ServiceThrows_ShouldPropagateException() {
        String password = "wrongPassword";
        String username = "invalidUser";

        when(loginService.login(password, username))
                .thenThrow(new RuntimeException("Authentication failed"));

        LoginDto dto = new LoginDto(password, username);

        assertThrows(RuntimeException.class, () -> loginController.login(dto));
        verify(loginService).login(password, username);
    }
}