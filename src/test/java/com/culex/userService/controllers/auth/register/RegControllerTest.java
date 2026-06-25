package com.culex.userService.controllers.auth.register;

import com.culex.userService.DB.entities.User;
import com.culex.userService.service.auth.register.RegService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("RegController Unit Tests")
public class RegControllerTest {

    @MockitoBean
    private RegService regService;

    @Autowired
    private RegController regController;

    @Test
    @DisplayName("register: should return CREATED with userId and username on success")
    void register_ValidRequest_ShouldReturnCreated() {
        String password = "SecurePass123!";
        String username = "newUser";
        String nickname = "CoolNick";
        String email = "user@example.com";
        Long expectedUserId = 42L;

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(expectedUserId);
        when(regService.createUser(password, username, nickname, email)).thenReturn(mockUser);

        RegDto dto = new RegDto(password, username, nickname, email);
        ResponseEntity<?> response = regController.register(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof RegResponse);
        RegResponse regResponse = (RegResponse) response.getBody();
        assertEquals(username, regResponse.username());
        assertEquals(expectedUserId, regResponse.userId());
        verify(regService).createUser(password, username, nickname, email);
    }

    @Test
    @DisplayName("register: should propagate exception when validation fails")
    void register_InvalidData_ShouldPropagateException() {
        String password = "weak";
        String username = "u";
        String nickname = "n";
        String email = "invalid";

        when(regService.createUser(password, username, nickname, email))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        RegDto dto = new RegDto(password, username, nickname, email);

        assertThrows(IllegalArgumentException.class, () -> regController.register(dto));
        verify(regService).createUser(password, username, nickname, email);
    }
}